package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.ResolveParameterHook.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.support.*;

public class DefaultContainerLifecycleContext extends AbstractLifecycleContext implements ContainerLifecycleContext {

	private final ContainerClassDescriptor classDescriptor;
	private final ParameterSupplierResolver parameterSupplierResolver;

	public DefaultContainerLifecycleContext(
		ContainerClassDescriptor classDescriptor,
		Reporter reporter,
		ResolveParameterHook resolveParameterHook
	) {
		super(reporter);
		this.classDescriptor = classDescriptor;
		this.parameterSupplierResolver = new ParameterSupplierResolver(resolveParameterHook);
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
	public Optional<Class<?>> containerClass() {
		return Optional.of(classDescriptor.getContainerClass());
	}

	@Override
	public <T> T newInstance(Class<T> clazz) {
		return JqwikReflectionSupport.newInstanceWithDefaultConstructor(clazz);
	}

	@Override
	public Optional<ParameterSupplier> resolveParameter(Method method, int index) {
		return containerClass().flatMap(containerClass -> parameterSupplierResolver.resolveParameter(method, index, containerClass));
	}

}
