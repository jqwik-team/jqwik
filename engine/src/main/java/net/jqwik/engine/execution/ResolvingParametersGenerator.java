package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.ResolveParameterHook.*;
import net.jqwik.engine.support.*;

public class ResolvingParametersGenerator implements ParametersGenerator {
	private final Map<Parameter, ParameterSupplier> resolvedSuppliers = new HashMap<>();
	private final List<MethodParameter> propertyParameters;
	private final Iterator<List<Shrinkable<Object>>> forAllParametersGenerator;
	private final ResolveParameterHook resolveParameterHook;

	public ResolvingParametersGenerator(
		List<MethodParameter> propertyParameters,
		Iterator<List<Shrinkable<Object>>> forAllParametersGenerator,
		ResolveParameterHook resolveParameterHook
	) {
		this.propertyParameters = propertyParameters;
		this.forAllParametersGenerator = forAllParametersGenerator;
		this.resolveParameterHook = resolveParameterHook;
	}

	@Override
	public boolean hasNext() {
		return forAllParametersGenerator.hasNext();
	}

	@Override
	public List<Shrinkable<Object>> next(TryLifecycleContext tryLifecycleContext) {
		List<Shrinkable<Object>> next = new ArrayList<>();
		List<Shrinkable<Object>> forAllShrinkables = new ArrayList<>(forAllParametersGenerator.next());

		for (MethodParameter parameter : propertyParameters) {
			if (parameter.isAnnotated(ForAll.class)) {
				next.add(forAllShrinkables.get(0));
				forAllShrinkables.remove(0);
			} else {
				next.add(findResolvableParameter(parameter, tryLifecycleContext));
			}
		}

		return next;
	}

	private Shrinkable<Object> findResolvableParameter(MethodParameter parameter, TryLifecycleContext tryLifecycleContext) {
		ParameterSupplier parameterSupplier =
			resolvedSuppliers.computeIfAbsent(parameter.getRawParameter(), ignore -> resolveSupplier(parameter));

		ParameterResolutionContext parameterContext = new DefaultParameterInjectionContext(parameter);
		return new ShrinkableResolvedParameter(parameterSupplier, parameterContext, tryLifecycleContext);
	}

	private ParameterSupplier resolveSupplier(MethodParameter parameter) {
		ParameterResolutionContext parameterContext = new DefaultParameterInjectionContext(parameter);
		Optional<ParameterSupplier> optionalSupplier = resolveParameterHook.resolve(parameterContext);
		return optionalSupplier.orElseThrow(
			() -> {
				String info = "No matching resolver could be found";
				return new CannotResolveParameterException(parameterContext, info);
			});
	}

}
