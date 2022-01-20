package net.jqwik.engine.execution;

import java.util.*;
import java.util.stream.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.execution.pipeline.*;
import net.jqwik.engine.execution.reporting.*;
import net.jqwik.engine.support.*;

class ContainerTaskCreator {

	ExecutionTask createTask(
		TestDescriptor containerDescriptor,
		ExecutionTaskCreator childTaskCreator,
		Pipeline pipeline,
		LifecycleHooksSupplier lifecycleSupplier,
		PropertyExecutionListener propertyExecutionListener
	) {

		Reporter reporter = new DefaultReporter(
			propertyExecutionListener::reportingEntryPublished,
			containerDescriptor,
			RegisteredSampleReportingFormats.getReportingFormats()
		);

		ContainerLifecycleContext containerLifecycleContext = createLifecycleContext(containerDescriptor, reporter, lifecycleSupplier);

		SkipExecutionHook.SkipResult skipResult = CurrentTestDescriptor.runWithDescriptor(containerDescriptor, () -> {
			// If SkipExecutionHook ran in task skipping of children wouldn't work
			SkipExecutionHook skipExecutionHook = lifecycleSupplier.skipExecutionHook(containerDescriptor);
			return skipExecutionHook.shouldBeSkipped(containerLifecycleContext);
		});

		if (skipResult.isSkipped()) {
			return ExecutionTask.from(
				(listener, ignorePredecessorResult) -> {
					listener.executionSkipped(containerDescriptor, skipResult.reason().orElse(null));
					return TaskExecutionResult.success();
				},
				containerDescriptor,
				"skip " + containerDescriptor.getDisplayName()
			);
		}

		BeforeContainerHook beforeContainerHook = lifecycleSupplier.beforeContainerHook(containerDescriptor);
		ExecutionTask prepareContainerTask = ExecutionTask.from(
			(listener, predecessorResult) -> {
				listener.executionStarted(containerDescriptor);
				try {
					beforeContainerHook.beforeContainer(containerLifecycleContext);
				} catch (Throwable throwable) {
					//noinspection ResultOfMethodCallIgnored
					JqwikExceptionSupport.throwAsUncheckedException(throwable);
				}
				return TaskExecutionResult.success();
			},
			containerDescriptor,
			"prepare " + containerDescriptor.getDisplayName()
		);

		ExecutionTask[] childrenTasks = createChildren(
			containerDescriptor.getChildren(),
			childTaskCreator,
			pipeline,
			propertyExecutionListener
		);
		for (ExecutionTask childTask : childrenTasks) {
			pipeline.submit(childTask, prepareContainerTask);
		}

		AfterContainerHook afterContainerHook = lifecycleSupplier.afterContainerHook(containerDescriptor);
		ExecutionTask finishContainerTask = ExecutionTask.from(
			(listener, predecessorResult) -> {
				PropertyExecutionResult propertyExecutionResult =
					predecessorResult.successful() ?
						PlainExecutionResult.successful() :
						PlainExecutionResult.failed(predecessorResult.throwable().orElse(null), null);
				try {
					afterContainerHook.afterContainer(containerLifecycleContext);
					return predecessorResult;
				} catch (Throwable throwable) {
					JqwikExceptionSupport.rethrowIfBlacklisted(throwable);
					propertyExecutionResult = propertyExecutionResult.mapToFailed(throwable);
					return TaskExecutionResult.failure(throwable);
				} finally {
					listener.executionFinished(containerDescriptor, propertyExecutionResult);

					// TODO: Move to AfterContainerExecutor as soon as there is one
					StoreRepository.getCurrent().finishScope(containerDescriptor);
				}
			},
			containerDescriptor,
			"finish " + containerDescriptor.getDisplayName()
		);

		if (childrenTasks.length == 0)
			pipeline.submit(finishContainerTask, prepareContainerTask);
		else
			pipeline.submit(finishContainerTask, childrenTasks);

		return prepareContainerTask;
	}

	private ContainerLifecycleContext createLifecycleContext(
		TestDescriptor containerDescriptor,
		Reporter reporter,
		LifecycleHooksSupplier lifecycleSupplier
	) {
		ResolveParameterHook resolveParameterHook = lifecycleSupplier.resolveParameterHook(containerDescriptor);
		if (containerDescriptor instanceof ContainerClassDescriptor) {
			ContainerClassDescriptor classDescriptor = (ContainerClassDescriptor) containerDescriptor;
			return new DefaultContainerLifecycleContext(classDescriptor, reporter, resolveParameterHook);
		} else if (containerDescriptor instanceof EngineDescriptor) {
			return new EngineLifecycleContext(containerDescriptor, reporter, resolveParameterHook);
		} else {
			String message = String.format("Unknown descriptor type for [%s]", containerDescriptor.getUniqueId());
			throw new JqwikException(message);
		}
	}

	private ExecutionTask[] createChildren(
		Set<? extends TestDescriptor> children,
		ExecutionTaskCreator childTaskCreator,
		Pipeline pipeline,
		PropertyExecutionListener propertyExecutionListener
	) {
		ExecutionTask[] childrenTasks = new ExecutionTask[0];
		return children.stream()
					   .map(child -> childTaskCreator.createTask(child, pipeline, propertyExecutionListener))
					   .collect(Collectors.toList()).toArray(childrenTasks);
	}
}
