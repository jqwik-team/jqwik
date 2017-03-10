package net.jqwik.matchers;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import net.jqwik.discovery.JqwikClassTestDescriptor;

class IsClassDescriptorFor extends ArgumentMatcher<JqwikClassTestDescriptor> {

	private final Class<?> containerClass;

	IsClassDescriptorFor(Class<?> containerClass) {
		this.containerClass = containerClass;
	}

	@Override
	public boolean matches(Object argument) {
		if (argument.getClass() != JqwikClassTestDescriptor.class)
			return false;
		JqwikClassTestDescriptor descriptor = (JqwikClassTestDescriptor) argument;
		return descriptor.getContainerClass() == containerClass;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("is JqwikClassTestDescriptor for " + containerClass.toString());
	}

}
