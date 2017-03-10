package net.jqwik.matchers;

import java.lang.reflect.Method;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import net.jqwik.discovery.JqwikExampleTestDescriptor;
import org.mockito.internal.util.Decamelizer;

class IsExampleDescriptorFor extends ArgumentMatcher<JqwikExampleTestDescriptor> {

	private final Method exampleMethod;

	IsExampleDescriptorFor(Method exampleMethod) {
		this.exampleMethod = exampleMethod;
	}

	@Override
	public boolean matches(Object argument) {
		if (argument.getClass() != JqwikExampleTestDescriptor.class)
			return false;
		JqwikExampleTestDescriptor descriptor = (JqwikExampleTestDescriptor) argument;
		return descriptor.getExampleMethod().equals(exampleMethod);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("is JqwikExampleTestDescriptor for " + exampleMethod.toString());
	}
}
