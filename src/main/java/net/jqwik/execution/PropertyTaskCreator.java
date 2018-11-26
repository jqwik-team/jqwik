package net.jqwik.execution;

import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.lifecycle.*;
import net.jqwik.execution.pipeline.*;

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
				TestExecutionResult executionResult = executeTestMethod(methodDescriptor, lifecycleSupplier, listener);
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

	private TestExecutionResult executeTestMethod(
		PropertyMethodDescriptor methodDescriptor, LifecycleSupplier lifecycleSupplier, JqwikExecutionListener listener
	) {
		PropertyMethodExecutor executor = new PropertyMethodExecutor(methodDescriptor);
		return executor.execute(lifecycleSupplier, listener);
	}

}
