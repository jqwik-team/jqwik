package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.ResolveParameterHook.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.support.*;

public class DefaultPropertyLifecycleContext extends AbstractLifecycleContext implements PropertyLifecycleContext {

	private final PropertyMethodDescriptor methodDescriptor;
	private final Object testInstance;
	private final Reporter reporter;
	private final ParameterSupplierResolver parameterSupplierResolver;

	public DefaultPropertyLifecycleContext(
		PropertyMethodDescriptor methodDescriptor,
		Object testInstance,
		Reporter reporter,
		ResolveParameterHook resolveParameterHook
	) {
		super(reporter);
		this.parameterSupplierResolver = new ParameterSupplierResolver(resolveParameterHook);
		this.methodDescriptor = methodDescriptor;
		this.testInstance = testInstance;
		this.reporter = reporter;
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
	public String label() {
		return methodDescriptor.getLabel();
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
		return testInstance;
	}

	@Override
	public Reporter reporter() {
		return reporter;
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

}
