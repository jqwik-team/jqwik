package net.jqwik.execution;

import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.properties.*;
import net.jqwik.descriptor.*;
import net.jqwik.support.*;

abstract public class AbstractMethodExecutor<T extends AbstractMethodDescriptor, U extends TestLifecycle> {

	public void execute(T methodDescriptor, EngineExecutionListener listener, Function<Object, U> lifecycleSupplier) {
		if (hasUnspecifiedParameters(methodDescriptor)) {
			listener.executionSkipped(methodDescriptor, "Cannot run methods with unbound parameters - yet.");
			return;
		}
		listener.executionStarted(methodDescriptor);
		TestExecutionResult executionResult = executeTestMethod(methodDescriptor, lifecycleSupplier, listener);
		listener.executionFinished(methodDescriptor, executionResult);
	}

	private boolean hasUnspecifiedParameters(T methodDescriptor) {
		return Arrays.stream(methodDescriptor.getTargetMethod().getParameters())
				.anyMatch(parameter -> !parameter.isAnnotationPresent(ForAll.class));
	}

	private TestExecutionResult executeTestMethod(T methodDescriptor, Function<Object, U> lifecycleSupplier,
			EngineExecutionListener listener) {
		Object testInstance = null;
		try {
			testInstance = createTestInstance(methodDescriptor);
		} catch (Throwable throwable) {
			String message = String.format("Cannot create instance of class '%s'. Maybe it has no default constructor?",
					methodDescriptor.getContainerClass());
			return TestExecutionResult.failed(new JqwikException(message, throwable));
		}
		return invokeTestMethod(methodDescriptor, testInstance, lifecycleSupplier, listener);
	}

	private Object createTestInstance(T methodDescriptor) {
		return JqwikReflectionSupport.newInstanceWithDefaultConstructor(methodDescriptor.getContainerClass());
	}

	private TestExecutionResult invokeTestMethod(T methodDescriptor, Object testInstance, Function<Object, U> lifecycleSupplier,
			EngineExecutionListener listener) {
		TestExecutionResult testExecutionResult = TestExecutionResult.successful();
		try {
			testExecutionResult = executeMethod(methodDescriptor, testInstance, listener);
		} finally {
			List<Throwable> throwableCollector = new ArrayList<>();
			lifecycleDoFinally(methodDescriptor, testInstance, lifecycleSupplier, throwableCollector);
			if (!throwableCollector.isEmpty() && testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
				// TODO: Use MultiException for reporting all exceptions
				testExecutionResult = TestExecutionResult.failed(throwableCollector.get(0));
			}
		}
		return testExecutionResult;
	}

	private void lifecycleDoFinally(T methodDescriptor, Object testInstance, Function<Object, U> lifecycleSupplier,
			List<Throwable> throwableCollector) {

		JqwikReflectionSupport.streamInnerInstances(testInstance).forEach(innerInstance -> {
			try {
				U lifecycle = lifecycleSupplier.apply(innerInstance);
				lifecycle.doFinally(methodDescriptor, innerInstance);
			} catch (Throwable throwable) {
				throwableCollector.add(throwable);
			}
		});
	}

	protected abstract TestExecutionResult executeMethod(T methodDescriptor, Object testInstance, EngineExecutionListener listener);

}
