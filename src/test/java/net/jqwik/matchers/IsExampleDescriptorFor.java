package net.jqwik.matchers;

import java.lang.reflect.Method;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import net.jqwik.descriptor.ExampleMethodDescriptor;

class IsExampleDescriptorFor extends ArgumentMatcher<ExampleMethodDescriptor> {

	private final Method exampleMethod;

	IsExampleDescriptorFor(Method exampleMethod) {
		this.exampleMethod = exampleMethod;
	}

	@Override
	public boolean matches(Object argument) {
		if (argument.getClass() != ExampleMethodDescriptor.class)
			return false;
		ExampleMethodDescriptor descriptor = (ExampleMethodDescriptor) argument;
		return descriptor.getExampleMethod().equals(exampleMethod);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("is ExampleMethodDescriptor for " + exampleMethod.toString());
	}
}
