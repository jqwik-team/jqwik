package net.jqwik.engine.execution;

import java.lang.reflect.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.SkipExecutionHook.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.execution.pipeline.*;
import net.jqwik.engine.execution.reporting.*;
import net.jqwik.engine.support.*;

class PropertyTaskCreator {

	ExecutionTask createTask(
		PropertyMethodDescriptor methodDescriptor,
		LifecycleHooksSupplier lifecycleSupplier,
		boolean reportOnlyFailures
	) {
		return ExecutionTask.from(
			(listener, predecessorResult) -> {

				if (!predecessorResult.successful()) {
					String reason = String.format("Skipped due to container failure: %s", predecessorResult.throwable().orElse(null));
					listener.executionSkipped(methodDescriptor, reason);
					return predecessorResult;
				}

				PropertyLifecycleContext propertyLifecycleContext;

				try {
					propertyLifecycleContext = createLifecycleContext(methodDescriptor, lifecycleSupplier, listener);

					SkipResult skipResult = CurrentTestDescriptor.runWithDescriptor(methodDescriptor, () -> {
						SkipExecutionHook skipExecutionHook = lifecycleSupplier.skipExecutionHook(methodDescriptor);
						return skipExecutionHook.shouldBeSkipped(propertyLifecycleContext);
					});

					if (skipResult.isSkipped()) {
						listener.executionSkipped(methodDescriptor, skipResult.reason().orElse(null));
						return TaskExecutionResult.success();
					}

					listener.executionStarted(methodDescriptor);

					try {
						DomainContext domainContext = createDomainContext(methodDescriptor, propertyLifecycleContext);
						CurrentDomainContext.runWithContext(domainContext, () -> {
							PropertyExecutionResult executionResult = executeTestMethod(
								methodDescriptor, propertyLifecycleContext, lifecycleSupplier, reportOnlyFailures
							);
							listener.executionFinished(methodDescriptor, executionResult);
							return null;
						});
					} finally {
						StoreRepository.getCurrent().finishScope(methodDescriptor);
					}

				} catch (Throwable throwable) {
					JqwikExceptionSupport.rethrowIfBlacklisted(throwable);
					handleExceptionDuringTestInstanceCreation(methodDescriptor, listener, throwable);
				}

				return TaskExecutionResult.success();
			},
			methodDescriptor,
			"executing " + methodDescriptor.getDisplayName()
		);
	}

	private DomainContext createDomainContext(
		PropertyMethodDescriptor methodDescriptor,
		PropertyLifecycleContext propertyLifecycleContext
	) {
		DomainContextFactory domainContextFactory = new DomainContextFactory(propertyLifecycleContext, methodDescriptor);
		return domainContextFactory.createCombinedDomainContext();
	}

	private PropertyLifecycleContext createLifecycleContext(
		PropertyMethodDescriptor methodDescriptor,
		LifecycleHooksSupplier lifecycleSupplier,
		PropertyExecutionListener listener
	) {
		PropertyLifecycleContext propertyLifecycleContext;
		ResolveParameterHook resolveParameterHook = lifecycleSupplier.resolveParameterHook(methodDescriptor);
		Reporter reporter = new DefaultReporter(listener::reportingEntryPublished, methodDescriptor);
		ContainerInstances testInstances = createTestInstances(methodDescriptor, lifecycleSupplier, reporter);
		propertyLifecycleContext = new DefaultPropertyLifecycleContext(methodDescriptor, testInstances, reporter, resolveParameterHook);
		return propertyLifecycleContext;
	}

	private void handleExceptionDuringTestInstanceCreation(
		PropertyMethodDescriptor methodDescriptor,
		PropertyExecutionListener listener,
		Throwable throwable
	) {
		listener.executionStarted(methodDescriptor);
		PropertyExecutionResult executionResult =
			PlainExecutionResult.failed(throwable, methodDescriptor.getConfiguration().getSeed());
		listener.executionFinished(methodDescriptor, executionResult);
	}

	private ContainerInstances createTestInstances(
		PropertyMethodDescriptor methodDescriptor,
		LifecycleHooksSupplier lifecycleSupplier,
		Reporter reporter
	) {
		try {
			return methodDescriptor.getParent().map(
				containerDescriptor -> {
					//TODO: Hand context in from outside to always have the same instance.
					//  This will require a major overhaul of task creation :-(
					ResolveParameterHook resolveParameterHook = lifecycleSupplier.resolveParameterHook(containerDescriptor);
					ProvidePropertyInstanceHook providePropertyInstanceHook = lifecycleSupplier.providePropertyInstanceHook(containerDescriptor);
					ContainerLifecycleContext containerLifecycleContext = new DefaultContainerLifecycleContext(
						(ContainerClassDescriptor) containerDescriptor,
						reporter,
						resolveParameterHook
					);
					return CurrentTestDescriptor.runWithDescriptor(
						containerDescriptor,
						() -> createTestInstancesWithResolvedParameters(
							containerLifecycleContext,
							(ContainerClassDescriptor) containerDescriptor,
							providePropertyInstanceHook
						)
					);
				}).orElseThrow(() -> new JqwikException("Method descriptors must have a parent"));
		} catch (JqwikException jqwikException) {
			throw jqwikException;
		} catch (Throwable throwable) {
			JqwikExceptionSupport.rethrowIfBlacklisted(throwable);
			String message = String.format(
				"Cannot create instance of class '%s'",
				methodDescriptor.getContainerClass()
			);
			if (throwable instanceof InvocationTargetException) {
				throwable = ((InvocationTargetException) throwable).getTargetException();
			}
			throw new JqwikException(message, throwable);
		}
	}

	private ContainerInstances createTestInstancesWithResolvedParameters(
		ContainerLifecycleContext containerLifecycleContext,
		ContainerClassDescriptor containerDescriptor,
		ProvidePropertyInstanceHook providePropertyInstanceHook
	) {
		TestInstancesCreator testInstanceCreator = new TestInstancesCreator(
			containerLifecycleContext,
			containerDescriptor,
			providePropertyInstanceHook
		);
		return testInstanceCreator.create();
	}

	private PropertyExecutionResult executeTestMethod(
		PropertyMethodDescriptor methodDescriptor,
		PropertyLifecycleContext propertyLifecycleContext,
		LifecycleHooksSupplier lifecycleSupplier,
		boolean reportOnlyFailures
	) {
		PropertyMethodExecutor executor = new PropertyMethodExecutor(methodDescriptor, propertyLifecycleContext, reportOnlyFailures);
		return executor.execute(lifecycleSupplier);
	}

}
