package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.execution.pipeline.*;

class PropertyTaskCreator {

	ExecutionTask createTask(PropertyMethodDescriptor methodDescriptor, LifecycleSupplier lifecycleSupplier) {
		if (hasUnspecifiedParameters(methodDescriptor)) {
			String taskDescription = "skipping " + methodDescriptor.getDisplayName();
			return ExecutionTask.from(
				listener -> listener.executionSkipped(methodDescriptor, "Cannot run methods with unbound parameters - yet."),
				methodDescriptor.getUniqueId(),
				taskDescription
			);
		}
		return ExecutionTask.from(
			listener -> {
				listener.executionStarted(methodDescriptor);
				PropertyExecutionResult executionResult = executeTestMethod(methodDescriptor, lifecycleSupplier, listener);
				listener.executionFinished(methodDescriptor, executionResult);
			},
			methodDescriptor.getUniqueId(),
			"executing " + methodDescriptor.getDisplayName()
		);
	}

	private boolean hasUnspecifiedParameters(PropertyMethodDescriptor methodDescriptor) {
		return Arrays.stream(methodDescriptor.getTargetMethod().getParameters())
					 .anyMatch(parameter -> !parameter.isAnnotationPresent(ForAll.class));
	}

	private PropertyExecutionResult executeTestMethod(
		PropertyMethodDescriptor methodDescriptor, LifecycleSupplier lifecycleSupplier, PropertyExecutionListener listener
	) {
		PropertyMethodExecutor executor = new PropertyMethodExecutor(methodDescriptor);
		return executor.execute(lifecycleSupplier, listener);
	}

}
