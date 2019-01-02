package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.SkipExecutionHook.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.execution.pipeline.*;
import net.jqwik.engine.support.*;

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
				Object testInstance = createTestInstance(methodDescriptor);
				PropertyLifecycleContext propertyLifecycleContext = new PropertyMethodLifecycleContext(methodDescriptor, testInstance);

				SkipExecutionHook skipExecutionHook = lifecycleSupplier.skipExecutionHook(methodDescriptor);
				SkipResult skipResult = skipExecutionHook.shouldBeSkipped(propertyLifecycleContext);

				if (skipResult.isSkipped()) {
					listener.executionSkipped(methodDescriptor, skipResult.reason().orElse(null));
					return;
				}

				listener.executionStarted(methodDescriptor);
				PropertyExecutionResult executionResult = executeTestMethod(methodDescriptor, propertyLifecycleContext, lifecycleSupplier, listener);
				listener.executionFinished(methodDescriptor, executionResult);
			},
			methodDescriptor.getUniqueId(),
			"executing " + methodDescriptor.getDisplayName()
		);
	}

	private Object createTestInstance(PropertyMethodDescriptor methodDescriptor) {
		try {
			return JqwikReflectionSupport.newInstanceWithDefaultConstructor(methodDescriptor.getContainerClass());
		} catch (Throwable throwable) {
			String message = String.format(
				"Cannot create instance of class '%s'. Maybe it has no default constructor?",
				methodDescriptor.getContainerClass()
			);
			throw new JqwikException(message, throwable);
		}
	}

	private boolean hasUnspecifiedParameters(PropertyMethodDescriptor methodDescriptor) {
		return Arrays.stream(methodDescriptor.getTargetMethod().getParameters())
					 .anyMatch(parameter -> !parameter.isAnnotationPresent(ForAll.class));
	}

	private PropertyExecutionResult executeTestMethod(
		PropertyMethodDescriptor methodDescriptor,
		PropertyLifecycleContext propertyLifecycleContext,
		LifecycleSupplier lifecycleSupplier,
		PropertyExecutionListener listener
	) {
		PropertyMethodExecutor executor = new PropertyMethodExecutor(methodDescriptor, propertyLifecycleContext);
		return executor.execute(lifecycleSupplier, listener);
	}

}
