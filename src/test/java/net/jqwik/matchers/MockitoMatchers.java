package net.jqwik.matchers;

import static org.mockito.Matchers.*;

import net.jqwik.descriptor.PropertyMethodDescriptor;
import org.junit.platform.engine.TestExecutionResult;

import net.jqwik.descriptor.ContainerClassDescriptor;
import net.jqwik.descriptor.ExampleMethodDescriptor;
import net.jqwik.descriptor.OverloadedExampleMethodDescriptor;

public class MockitoMatchers {

	public static ContainerClassDescriptor isClassDescriptorFor(Class<?> containerClass) {
		return argThat(new IsClassDescriptorFor(containerClass));
	}

	public static ExampleMethodDescriptor isExampleDescriptorFor(Class<?> containerClass, String methodName) {
		return argThat(new IsExampleDescriptorFor(containerClass, methodName));
	}

	public static PropertyMethodDescriptor isPropertyDescriptorFor(Class<?> containerClass, String methodName) {
		return argThat(new IsPropertyDescriptorFor(containerClass, methodName));
	}

	public static TestExecutionResult isSuccessful() {
		return eq(TestExecutionResult.successful());
	}

	public static TestExecutionResult isFailed(String message) {
		return argThat(new IsTestResultFailure(message));
	}

	public static TestExecutionResult isFailed() {
		return isFailed(null);
	}

}
