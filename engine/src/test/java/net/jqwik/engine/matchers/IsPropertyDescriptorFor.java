package net.jqwik.engine.matchers;

import org.mockito.*;

import net.jqwik.engine.descriptor.*;

class IsPropertyDescriptorFor implements ArgumentMatcher<PropertyMethodDescriptor> {

	private final Class<?> containerClass;
	private final String methodName;

	IsPropertyDescriptorFor(Class<?> containerClass, String methodName) {
		this.containerClass = containerClass;
		this.methodName = methodName;
	}

	@Override
	public boolean matches(PropertyMethodDescriptor descriptor) {
		return descriptor.getContainerClass().equals(containerClass) //
				&& descriptor.getTargetMethod().getName().equals(methodName);
	}

	@Override
	public String toString() {
		return "IsPropertyDescriptorFor(" + containerClass + "." + methodName + ")";
	}
}
