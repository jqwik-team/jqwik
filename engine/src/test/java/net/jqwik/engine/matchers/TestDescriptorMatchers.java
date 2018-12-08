package net.jqwik.engine.matchers;

import net.jqwik.engine.descriptor.*;

import static org.mockito.ArgumentMatchers.*;

public class TestDescriptorMatchers {

	public static ContainerClassDescriptor isClassDescriptorFor(Class<?> containerClass) {
		return argThat(new IsClassDescriptorFor(containerClass));
	}

	public static PropertyMethodDescriptor isPropertyDescriptorFor(Class<?> containerClass, String methodName) {
		return argThat(new IsPropertyDescriptorFor(containerClass, methodName));
	}
}
