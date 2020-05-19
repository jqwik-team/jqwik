package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.ResolveParameterHook.*;
import net.jqwik.engine.support.*;

public class ResolvingParametersGenerator implements ParametersGenerator {
	private final List<MethodParameter> propertyParameters;
	private final Iterator<List<Shrinkable<Object>>> forAllParametersGenerator;
	private final ParameterSupplierResolver parameterSupplierResolver;

	public ResolvingParametersGenerator(
		List<MethodParameter> propertyParameters,
		Iterator<List<Shrinkable<Object>>> forAllParametersGenerator,
		ResolveParameterHook resolveParameterHook,
		PropertyLifecycleContext propertyLifecycleContext
	) {
		this.propertyParameters = propertyParameters;
		this.forAllParametersGenerator = forAllParametersGenerator;
		this.parameterSupplierResolver = new ParameterSupplierResolver(resolveParameterHook, propertyLifecycleContext);
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

	@Override
	public int edgeCasesTotal() {
		return 42; // TODO
	}

	@Override
	public int edgeCasesTried() {
		return 17; // TODO
	}

	private Shrinkable<Object> findResolvableParameter(MethodParameter parameter, TryLifecycleContext tryLifecycleContext) {
		ParameterSupplier parameterSupplier =
			parameterSupplierResolver.resolveParameter(parameter).orElseThrow(() -> {
				String info = "No matching resolver could be found";
				return new CannotResolveParameterException(parameter.getRawParameter(), info);
			});
		ParameterResolutionContext parameterContext = new DefaultParameterInjectionContext(parameter);
		return new ShrinkableResolvedParameter(parameterSupplier, parameterContext, tryLifecycleContext);
	}

}
