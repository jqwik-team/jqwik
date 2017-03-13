package net.jqwik.matchers;

import net.jqwik.discovery.ContainerClassDescriptor;
import net.jqwik.discovery.ExampleMethodDescriptor;
import org.junit.platform.commons.support.MethodSortOrder;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.TestExecutionResult;

import java.lang.reflect.Method;
import java.util.List;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;

public class MockitoMatchers {

	public static ContainerClassDescriptor isClassDescriptorFor(Class<?> containerClass) {
		return argThat(new IsClassDescriptorFor(containerClass));
	}

	public static ExampleMethodDescriptor isExampleDescriptorFor(Class<?> containerClass, String methodName) {
		List<Method> methods = ReflectionSupport.findMethods(containerClass, m -> m.getName().equals(methodName), MethodSortOrder.HierarchyUp);
		return argThat(new IsExampleDescriptorFor(methods.get(0)));
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
