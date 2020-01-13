package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.execution.pipeline.*;

class ContainerTaskCreator {

	ExecutionTask createTask(
		TestDescriptor containerDescriptor,
		ExecutionTaskCreator childTaskCreator,
		Pipeline pipeline,
		LifecycleHooksSupplier lifecycleSupplier
	) {

		// If SkipExecutionHook ran in task skipping of children wouldn't work
		ContainerLifecycleContext containerLifecycleContext = createLifecycleContext(containerDescriptor);
		SkipExecutionHook skipExecutionHook = lifecycleSupplier.skipExecutionHook(containerDescriptor);
		SkipExecutionHook.SkipResult skipResult = skipExecutionHook.shouldBeSkipped(containerLifecycleContext);

		if (skipResult.isSkipped()) {
			return ExecutionTask.from(
				listener -> listener.executionSkipped(containerDescriptor, skipResult.reason().orElse(null)),
				containerDescriptor,
				"skip " + containerDescriptor.getDisplayName()
			);
		}

		ExecutionTask prepareContainerTask = ExecutionTask.from(
			listener -> listener.executionStarted(containerDescriptor),
			containerDescriptor,
			"prepare " + containerDescriptor.getDisplayName()
		);

		ExecutionTask[] childrenTasks = createChildren(containerDescriptor.getChildren(), childTaskCreator, pipeline);
		for (ExecutionTask childTask : childrenTasks) {
			pipeline.submit(childTask, prepareContainerTask);
		}

		ExecutionTask finishContainerTask = ExecutionTask.from(
			listener -> {
				// TODO: Check predecessor results first: use SafeExecutor?
				PropertyExecutionResult result = PropertyExecutionResult.successful();
				listener.executionFinished(containerDescriptor, result);
				StoreRepository.getCurrent().removeStoresFor(containerDescriptor);
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

	private ContainerLifecycleContext createLifecycleContext(TestDescriptor containerDescriptor) {
		if (containerDescriptor instanceof ContainerClassDescriptor) {
			ContainerClassDescriptor classDescriptor = (ContainerClassDescriptor) containerDescriptor;
			return new ContainerClassLifecycleContext(classDescriptor);
		}

		return new ContainerLifecycleContext() {
			@Override
			public String label() {
				return containerDescriptor.getDisplayName();
			}

			@Override
			public Optional<AnnotatedElement> annotatedElement() {
				return Optional.empty();
			}
		};
	}

	private ExecutionTask[] createChildren(
		Set<? extends TestDescriptor> children,
		ExecutionTaskCreator childTaskCreator,
		Pipeline pipeline
	) {
		ExecutionTask[] childrenTasks = new ExecutionTask[0];
		return children.stream() //
					   .map(child -> childTaskCreator.createTask(child, pipeline)) //
					   .collect(Collectors.toList()).toArray(childrenTasks);
	}
}
