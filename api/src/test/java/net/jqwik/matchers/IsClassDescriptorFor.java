package net.jqwik.matchers;

import org.mockito.*;

import net.jqwik.descriptor.*;

class IsClassDescriptorFor implements ArgumentMatcher<ContainerClassDescriptor> {

	private final Class<?> containerClass;

	IsClassDescriptorFor(Class<?> containerClass) {
		this.containerClass = containerClass;
	}

	@Override
	public boolean matches(ContainerClassDescriptor descriptor) {
		return descriptor.getContainerClass() == containerClass;
	}

}
