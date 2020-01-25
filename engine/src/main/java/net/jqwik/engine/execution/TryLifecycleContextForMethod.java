package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;

public class TryLifecycleContextForMethod implements TryLifecycleContext {
	private final PropertyLifecycleContext propertyContext;

	public TryLifecycleContextForMethod(PropertyLifecycleContext propertyContext) {
		this.propertyContext = propertyContext;
	}

	@Override
	public PropertyLifecycleContext propertyContext() {
		return propertyContext;
	}

	@Override
	public String label() {
		return propertyContext.label();
	}

	@Override
	public Optional<AnnotatedElement> annotatedElement() {
		return propertyContext.annotatedElement();
	}

	@Override
	public Reporter reporter() {
		return propertyContext.reporter();
	}
}
