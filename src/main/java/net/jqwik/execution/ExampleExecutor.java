package net.jqwik.execution;

import org.junit.platform.engine.TestExecutionResult;

import net.jqwik.descriptor.AbstractMethodDescriptor;
import net.jqwik.support.JqwikReflectionSupport;

public class ExampleExecutor extends AbstractMethodExecutor {

	@Override
	protected TestExecutionResult execute(AbstractMethodDescriptor methodDescriptor, Object testInstance) {
		SingleMethodExecutor.Executable executable = () -> JqwikReflectionSupport.invokeMethod(methodDescriptor.getTargetMethod(), testInstance);
		return new SingleMethodExecutor().executeSafely(executable);
	}

}
