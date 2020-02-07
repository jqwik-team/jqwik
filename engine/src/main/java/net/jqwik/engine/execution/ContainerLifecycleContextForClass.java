package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.descriptor.*;

public class ContainerLifecycleContextForClass implements ContainerLifecycleContext {
	private final ContainerClassDescriptor classDescriptor;
	private Reporter reporter;

	public ContainerLifecycleContextForClass(ContainerClassDescriptor classDescriptor, Reporter reporter) {
		this.classDescriptor = classDescriptor;
		this.reporter = reporter;
	}

	@Override
	public String label() {
		return classDescriptor.getDisplayName();
	}

	@Override
	public Optional<AnnotatedElement> annotatedElement() {
		return Optional.of(classDescriptor.getContainerClass());
	}

	@Override
	public Reporter reporter() {
		return reporter;
	}

	@Override
	public Optional<Class<?>> containerClass() {
		return Optional.of(classDescriptor.getContainerClass());
	}
}
