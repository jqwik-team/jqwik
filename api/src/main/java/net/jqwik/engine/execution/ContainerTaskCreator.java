package net.jqwik.engine.execution;

import java.util.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.pipeline.*;

class ContainerTaskCreator {

	ExecutionTask createTask(TestDescriptor containerDescriptor, ExecutionTaskCreator childTaskCreator, Pipeline pipeline) {
		ExecutionTask prepareContainerTask = ExecutionTask.from(
			listener -> listener.executionStarted(containerDescriptor),
			containerDescriptor.getUniqueId(),
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
			},
			containerDescriptor.getUniqueId(),
			"finish " + containerDescriptor.getDisplayName()
		);

		if (childrenTasks.length == 0)
			pipeline.submit(finishContainerTask, prepareContainerTask);
		else
			pipeline.submit(finishContainerTask, childrenTasks);

		return prepareContainerTask;
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
