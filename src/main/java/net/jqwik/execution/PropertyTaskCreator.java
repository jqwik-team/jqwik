package net.jqwik.execution;

import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.execution.pipeline.*;
import org.junit.platform.engine.*;

import java.util.*;
import java.util.function.*;

public class PropertyTaskCreator {

	public ExecutionTask createTask(PropertyMethodDescriptor methodDescriptor, Function<Object, PropertyLifecycle> lifecycleFunction) {
		if (hasUnspecifiedParameters(methodDescriptor)) {
			String taskDescription = "skipping " + methodDescriptor.getDisplayName();
			return ExecutionTask.from(
				listener -> listener.executionSkipped(methodDescriptor, "Cannot run methods with unbound parameters - yet."),
				methodDescriptor.getUniqueId(),
				taskDescription);
		}
		return ExecutionTask.from(listener -> {
				listener.executionStarted(methodDescriptor);
				TestExecutionResult executionResult = executeTestMethod(methodDescriptor, lifecycleFunction, listener);
				listener.executionFinished(methodDescriptor, executionResult);
			},
			methodDescriptor.getUniqueId(),
			"executing " + methodDescriptor.getDisplayName());
	}

	private boolean hasUnspecifiedParameters(PropertyMethodDescriptor methodDescriptor) {
		return Arrays.stream(methodDescriptor.getTargetMethod().getParameters())
			.anyMatch(parameter -> !parameter.isAnnotationPresent(ForAll.class));
	}

	private TestExecutionResult executeTestMethod(PropertyMethodDescriptor methodDescriptor,
												  Function<Object, PropertyLifecycle> lifecycleSupplier, EngineExecutionListener listener) {
		PropertyMethodExecutor executor = new PropertyMethodExecutor(methodDescriptor);
		return executor.execute(lifecycleSupplier, listener);
	}

}
