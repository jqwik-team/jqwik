package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.ResolveParameterHook.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.support.*;

public class DefaultPropertyLifecycleContext extends AbstractLifecycleContext implements PropertyLifecycleContext {

	private final PropertyMethodDescriptor methodDescriptor;
	private final TestInstances testInstances;
	private final ParameterSupplierResolver parameterSupplierResolver;

	public DefaultPropertyLifecycleContext(
		PropertyMethodDescriptor methodDescriptor,
		Object testInstance,
		Reporter reporter,
		ResolveParameterHook resolveParameterHook
	) {
		this(methodDescriptor, new TestInstances(testInstance), reporter, resolveParameterHook);
	}

	public DefaultPropertyLifecycleContext(
		PropertyMethodDescriptor methodDescriptor,
		TestInstances testInstances,
		Reporter reporter,
		ResolveParameterHook resolveParameterHook
	) {
		super(reporter, methodDescriptor);
		this.parameterSupplierResolver = new ParameterSupplierResolver(resolveParameterHook, this);
		this.methodDescriptor = methodDescriptor;
		this.testInstances = testInstances;
	}

	@Override
	public Method targetMethod() {
		return methodDescriptor.getTargetMethod();
	}

	@Override
	public Class<?> containerClass() {
		return methodDescriptor.getContainerClass();
	}

	@Override
	public Optional<AnnotatedElement> optionalElement() {
		return Optional.of(targetMethod());
	}

	@Override
	public Optional<Class<?>> optionalContainerClass() {
		return Optional.of(containerClass());
	}

	@Override
	public Object testInstance() {
		return testInstances.target();
	}

	@Override
	public List<Object> testInstances() {
		return JqwikReflectionSupport.getInstancesFromInside(testInstances.target());
	}

	@Override
	public String extendedLabel() {
		return methodDescriptor.extendedLabel();
	}

	@Override
	public <T> T newInstance(Class<T> clazz) {
		return JqwikReflectionSupport.newInstanceInTestContext(clazz, testInstance());
	}

	@Override
	public Optional<ParameterSupplier> resolveParameter(Executable executable, int index) {
		return parameterSupplierResolver.resolveParameter(executable, index, containerClass());
	}

	@Override
	public PropertyAttributes attributes() {
		return methodDescriptor.getConfiguration().getPropertyAttributes();
	}

	@Override
	public String toString() {
		return toString(PropertyLifecycleContext.class);
	}

}
