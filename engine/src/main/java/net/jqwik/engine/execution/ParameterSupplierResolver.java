package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.ResolveParameterHook.*;
import net.jqwik.engine.support.*;

class ParameterSupplierResolver {
	private final Map<Parameter, Optional<ParameterSupplier>> resolvedSuppliers = new LinkedHashMap<>();
	private final ResolveParameterHook resolveParameterHook;
	private final LifecycleContext lifecycleContext;

	ParameterSupplierResolver(ResolveParameterHook resolveParameterHook, LifecycleContext lifecycleContext) {
		this.resolveParameterHook = resolveParameterHook;
		this.lifecycleContext = lifecycleContext;
	}

	Optional<ParameterSupplier> resolveParameter(Executable executable, int index, Class<?> containerClass) {
		Parameter[] parameters = executable.getParameters();
		if (index >= 0 && index < parameters.length) {
			Parameter parameter = parameters[index];
			MethodParameter methodParameter = JqwikReflectionSupport.getMethodParameter(parameter, index, containerClass);
			return resolveParameter(methodParameter);
		} else {
			return Optional.empty();
		}
	}

	Optional<ParameterSupplier> resolveParameter(MethodParameter methodParameter) {
		return resolvedSuppliers.computeIfAbsent(methodParameter.getRawParameter(), ignore -> computeSupplier(methodParameter));
	}

	private Optional<ParameterSupplier> computeSupplier(MethodParameter methodParameter) {
		ParameterResolutionContext parameterContext = new DefaultParameterInjectionContext(methodParameter);
		return resolveParameterHook.resolve(parameterContext, lifecycleContext);
	}

}
