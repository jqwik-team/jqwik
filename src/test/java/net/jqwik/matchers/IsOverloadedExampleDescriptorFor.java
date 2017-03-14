package net.jqwik.matchers;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import net.jqwik.descriptor.OverloadedExampleMethodDescriptor;

class IsOverloadedExampleDescriptorFor extends ArgumentMatcher<OverloadedExampleMethodDescriptor> {

	private final Class<?> containerClass;
	private final String methodName;

	IsOverloadedExampleDescriptorFor(Class<?> containerClass, String methodName) {
		this.containerClass = containerClass;
		this.methodName = methodName;
	}

	@Override
	public boolean matches(Object argument) {
		if (argument.getClass() != OverloadedExampleMethodDescriptor.class)
			return false;
		OverloadedExampleMethodDescriptor descriptor = (OverloadedExampleMethodDescriptor) argument;
		return descriptor.gerContainerClass().equals(containerClass) && descriptor.getExampleMethod().getName().equals(methodName);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(String.format("is OverloadedExampleMethodDescriptor for %s::%s", containerClass, methodName));
	}
}
