package net.jqwik.matchers;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import net.jqwik.descriptor.ContainerClassDescriptor;

class IsClassDescriptorFor extends ArgumentMatcher<ContainerClassDescriptor> {

	private final Class<?> containerClass;

	IsClassDescriptorFor(Class<?> containerClass) {
		this.containerClass = containerClass;
	}

	@Override
	public boolean matches(Object argument) {
		if (argument.getClass() != ContainerClassDescriptor.class)
			return false;
		ContainerClassDescriptor descriptor = (ContainerClassDescriptor) argument;
		return descriptor.getContainerClass() == containerClass;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("is ContainerClassDescriptor for " + containerClass.toString());
	}

}
