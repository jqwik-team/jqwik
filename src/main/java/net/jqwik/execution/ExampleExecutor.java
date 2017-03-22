package net.jqwik.execution;

import net.jqwik.api.ExampleLifecycle;
import net.jqwik.descriptor.ExampleMethodDescriptor;
import net.jqwik.support.JqwikReflectionSupport;
import org.junit.platform.engine.TestExecutionResult;

public class ExampleExecutor extends AbstractMethodExecutor<ExampleMethodDescriptor, ExampleLifecycle> {

	@Override
	protected TestExecutionResult execute(ExampleMethodDescriptor methodDescriptor, Object testInstance) {
		SafeExecutor.Executable executable = () -> JqwikReflectionSupport.invokeMethod(methodDescriptor.getTargetMethod(), testInstance);
		return new SafeExecutor().executeSafely(executable);
	}

}
