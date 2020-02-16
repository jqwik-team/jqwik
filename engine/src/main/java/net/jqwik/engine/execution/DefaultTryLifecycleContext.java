package net.jqwik.engine.execution;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;

public class DefaultTryLifecycleContext implements TryLifecycleContext {
	private final PropertyLifecycleContext propertyContext;

	public DefaultTryLifecycleContext(PropertyLifecycleContext propertyContext) {
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

	@Override
	public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotationClass) {
		return propertyContext.findAnnotation(annotationClass);
	}

	@Override
	public <T> T newInstance(Class<T> clazz) {
		return propertyContext.newInstance(clazz);
	}
}
