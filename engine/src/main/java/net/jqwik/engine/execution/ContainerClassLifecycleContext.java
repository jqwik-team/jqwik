package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;

public class ContainerClassLifecycleContext implements ContainerLifecycleContext {
	private final ContainerClassDescriptor classDescriptor;

	public ContainerClassLifecycleContext(ContainerClassDescriptor classDescriptor) {
		this.classDescriptor = classDescriptor;
	}

	@Override
	public String label() {
		return classDescriptor.getDisplayName();
	}

	@Override
	public Optional<AnnotatedElement> annotatedElement() {
		return Optional.of(classDescriptor.getContainerClass());
	}
}
