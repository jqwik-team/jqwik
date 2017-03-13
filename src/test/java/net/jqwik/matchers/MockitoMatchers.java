package net.jqwik.matchers;

import static org.mockito.Matchers.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.TestExecutionResult;

import net.jqwik.discovery.ContainerClassDescriptor;
import net.jqwik.discovery.ExampleMethodDescriptor;
import net.jqwik.discovery.OverloadedExamplesError;

public class MockitoMatchers {

	public static ContainerClassDescriptor isClassDescriptorFor(Class<?> containerClass) {
		return argThat(new IsClassDescriptorFor(containerClass));
	}

	public static ExampleMethodDescriptor isExampleDescriptorFor(Class<?> containerClass, String methodName) {
		List<Method> methods = ReflectionSupport.findMethods(containerClass, m -> m.getName().equals(methodName), HierarchyTraversalMode.BOTTOM_UP);
		return argThat(new IsExampleDescriptorFor(methods.get(0)));
	}

	public static OverloadedExamplesError isOverloadedExamplesErrorFor(Class<?> containerClass, String overloadedMethodName) {
		return argThat(new IsOverloadedExamplesErrorFor(containerClass, overloadedMethodName));
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
