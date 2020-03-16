package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.ResolveParameterHook.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.support.*;

public class DefaultPropertyLifecycleContext extends AbstractLifecycleContext implements PropertyLifecycleContext {
	private final Map<Parameter, Optional<ParameterSupplier>> resolvedSuppliers = new HashMap<>();

	private final PropertyMethodDescriptor methodDescriptor;
	private final Object testInstance;
	private final Reporter reporter;

	public DefaultPropertyLifecycleContext(
		PropertyMethodDescriptor methodDescriptor,
		Object testInstance,
		Reporter reporter,
		ResolveParameterHook resolveParameterHook
	) {
		super(reporter, resolveParameterHook);
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
	public Optional<AnnotatedElement> annotatedElement() {
		return Optional.of(targetMethod());
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
	public Optional<ParameterSupplier> resolveParameter(Method method, int index) {
		Parameter[] parameters = method.getParameters();
		if (index >= 0 && index < parameters.length) {
			Parameter parameter = parameters[index];
			return resolvedSuppliers.computeIfAbsent(parameter, ignore -> resolveSupplier(parameter, index));
		}
		return Optional.empty();
	}

	private Optional<ParameterSupplier> resolveSupplier(Parameter parameter, int index) {
		MethodParameter methodParameter = JqwikReflectionSupport.getMethodParameter(parameter, index, containerClass());
		ParameterResolutionContext parameterContext = new DefaultParameterInjectionContext(methodParameter);
		return resolveParameterHook.resolve(parameterContext);
	}

}
