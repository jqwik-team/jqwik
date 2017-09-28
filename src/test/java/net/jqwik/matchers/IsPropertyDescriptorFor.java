package net.jqwik.matchers;

import org.mockito.*;

import net.jqwik.descriptor.*;

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

}
