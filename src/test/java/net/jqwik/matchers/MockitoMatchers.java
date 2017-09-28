package net.jqwik.matchers;

import net.jqwik.descriptor.*;
import org.junit.platform.engine.*;

import static org.mockito.ArgumentMatchers.*;

public class MockitoMatchers {

	public static ContainerClassDescriptor isClassDescriptorFor(Class<?> containerClass) {
		return argThat(new IsClassDescriptorFor(containerClass));
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

	public static TestExecutionResult isAborted() {
		return argThat(new IsTestResultAbortion(null));
	}

}
