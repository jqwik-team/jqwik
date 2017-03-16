package net.jqwik.matchers;

import net.jqwik.descriptor.PropertyMethodDescriptor;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

class IsPropertyDescriptorFor extends ArgumentMatcher<PropertyMethodDescriptor> {

	private final Class<?> containerClass;
	private final String methodName;

	IsPropertyDescriptorFor(Class<?> containerClass, String methodName) {
		this.containerClass = containerClass;
		this.methodName = methodName;
	}

	@Override
	public boolean matches(Object argument) {
		if (argument.getClass() != PropertyMethodDescriptor.class)
			return false;
		PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) argument;
		return descriptor.getContainerClass().equals(containerClass) && descriptor.getTargetMethod().getName().equals(methodName);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(String.format("is PropertyMethodDescriptor for %s::%s", containerClass, methodName));
	}
}
