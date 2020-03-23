package net.jqwik.engine.execution;

import java.util.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.SkipExecutionHook.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.execution.pipeline.*;
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
					ResolveParameterHook resolveParameterHook = lifecycleSupplier.resolveParameterHook(methodDescriptor);
					Object testInstance = createTestInstance(methodDescriptor, resolveParameterHook);
					Reporter reporter = (key, value) -> listener.reportingEntryPublished(methodDescriptor, ReportEntry.from(key, value));
					propertyLifecycleContext = new DefaultPropertyLifecycleContext(methodDescriptor, testInstance, reporter, resolveParameterHook);

					lifecycleSupplier.prepareHooks(methodDescriptor, propertyLifecycleContext);

					SkipExecutionHook skipExecutionHook = lifecycleSupplier.skipExecutionHook(methodDescriptor);
					SkipResult skipResult = skipExecutionHook.shouldBeSkipped(propertyLifecycleContext);

					if (skipResult.isSkipped()) {
						listener.executionSkipped(methodDescriptor, skipResult.reason().orElse(null));
						return TaskExecutionResult.success();
					}
				} catch (Throwable throwable) {
					handleExceptionDuringTestInstanceCreation(methodDescriptor, listener, throwable);
					return TaskExecutionResult.success();
				}

				listener.executionStarted(methodDescriptor);
				PropertyExecutionResult executionResult = executeTestMethod(
					methodDescriptor, propertyLifecycleContext, lifecycleSupplier, listener, reportOnlyFailures
				);
				listener.executionFinished(methodDescriptor, executionResult);

				return TaskExecutionResult.success();
			},
			methodDescriptor,
			"executing " + methodDescriptor.getDisplayName()
		);
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

	private Object createTestInstance(
		PropertyMethodDescriptor methodDescriptor,
		ResolveParameterHook resolveParameterHook
	) {
		try {
			if (methodDescriptor.getParent().isPresent()) {
				TestDescriptor container = methodDescriptor.getParent().get();
				return CurrentTestDescriptor.runWithDescriptor(
					container,
					() -> createTestInstanceWithResolvedParameters(methodDescriptor.getContainerClass(), resolveParameterHook)
				);
			} else {
				// Should only occur in tests
				return createTestInstanceWithResolvedParameters(methodDescriptor.getContainerClass(), resolveParameterHook);
			}
		} catch (JqwikException jqwikException) {
			throw jqwikException;
		} catch (Throwable throwable) {
			JqwikExceptionSupport.rethrowIfBlacklisted(throwable);
			String message = String.format(
				"Cannot create instance of class '%s'. Maybe it has no accessible constructor?",
				methodDescriptor.getContainerClass()
			);
			throw new JqwikException(message, throwable);
		}
	}

	private Object createTestInstanceWithResolvedParameters(
		Class<?> containerClass,
		ResolveParameterHook resolveParameterHook
	) {
		if (containerClass.getConstructors().length > 1) {
			String message = String.format("Test container class [%s] has more than one potential constructor", containerClass.getName());
			throw new JqwikException(message);
		}
		return JqwikReflectionSupport.newInstanceWithDefaultConstructor(containerClass);
	}

	private PropertyExecutionResult executeTestMethod(
		PropertyMethodDescriptor methodDescriptor,
		PropertyLifecycleContext propertyLifecycleContext,
		LifecycleHooksSupplier lifecycleSupplier,
		PropertyExecutionListener listener,
		boolean reportOnlyFailures
	) {
		PropertyMethodExecutor executor = new PropertyMethodExecutor(methodDescriptor, propertyLifecycleContext, reportOnlyFailures);
		return executor.execute(lifecycleSupplier, listener);
	}

}
