package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;

public class DefaultTryLifecycleContext extends AbstractLifecycleContext implements TryLifecycleContext {
	private final PropertyLifecycleContext propertyContext;

	public DefaultTryLifecycleContext(PropertyLifecycleContext propertyContext, ResolveParameterHook resolveParameterHook) {
		super(propertyContext.reporter());
		this.propertyContext = propertyContext;
	}

	@Override
	public Method targetMethod() {
		return propertyContext.targetMethod();
	}

	@Override
	public Class<?> containerClass() {
		return propertyContext.containerClass();
	}

	@Override
	public Object testInstance() {
		return propertyContext.testInstance();
	}

	@Override
	public String label() {
		return propertyContext.label();
	}

	@Override
	public Optional<AnnotatedElement> optionalElement() {
		return propertyContext.optionalElement();
	}

	@Override
	public Optional<Class<?>> optionalContainerClass() {
		return Optional.of(propertyContext.containerClass());
	}

	@Override
	public <T> T newInstance(Class<T> clazz) {
		return propertyContext.newInstance(clazz);
	}

	@Override
	public Optional<ResolveParameterHook.ParameterSupplier> resolveParameter(Executable executable, int index) {
		return propertyContext.resolveParameter(executable, index);
	}
}
