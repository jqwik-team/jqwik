package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

class ParameterSupplierResolver {
	private final Map<Parameter, Optional<ResolveParameterHook.ParameterSupplier>> resolvedSuppliers = new HashMap<>();
	private final ResolveParameterHook resolveParameterHook;

	ParameterSupplierResolver(ResolveParameterHook resolveParameterHook) {
		this.resolveParameterHook = resolveParameterHook;
	}

	Optional<ResolveParameterHook.ParameterSupplier> resolveParameter(Method method, int index, Class<?> containerClass) {
		Parameter[] parameters = method.getParameters();
		if (index >= 0 && index < parameters.length) {
			Parameter parameter = parameters[index];
			return resolvedSuppliers.computeIfAbsent(parameter, ignore -> resolveSupplier(parameter, index, containerClass));
		} else {
			return Optional.empty();
		}
	}

	private Optional<ResolveParameterHook.ParameterSupplier> resolveSupplier(
		Parameter parameter,
		int index,
		Class<?> containerClass
	) {
		MethodParameter methodParameter = JqwikReflectionSupport.getMethodParameter(parameter, index, containerClass);
		ParameterResolutionContext parameterContext = new DefaultParameterInjectionContext(methodParameter);
		return resolveParameterHook.resolve(parameterContext);
	}

}
