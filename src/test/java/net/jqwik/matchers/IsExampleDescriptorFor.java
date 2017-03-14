package net.jqwik.matchers;

import net.jqwik.descriptor.ExampleMethodDescriptor;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import net.jqwik.descriptor.OverloadedExampleMethodDescriptor;

class IsExampleDescriptorFor extends ArgumentMatcher<ExampleMethodDescriptor> {

	private final Class<?> containerClass;
	private final String methodName;

	IsExampleDescriptorFor(Class<?> containerClass, String methodName) {
		this.containerClass = containerClass;
		this.methodName = methodName;
	}

	@Override
	public boolean matches(Object argument) {
		if (argument.getClass() != ExampleMethodDescriptor.class)
			return false;
		ExampleMethodDescriptor descriptor = (ExampleMethodDescriptor) argument;
		return descriptor.gerContainerClass().equals(containerClass) && descriptor.getExampleMethod().getName().equals(methodName);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(String.format("is ExampleMethodDescriptor for %s::%s", containerClass, methodName));
	}
}
