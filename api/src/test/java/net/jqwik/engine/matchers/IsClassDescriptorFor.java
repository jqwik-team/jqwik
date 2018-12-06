package net.jqwik.engine.matchers;

import org.mockito.*;

import net.jqwik.engine.descriptor.*;

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
