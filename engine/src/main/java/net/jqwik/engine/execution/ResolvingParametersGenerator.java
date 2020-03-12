package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.lifecycle.ResolveParameterHook.*;
import net.jqwik.engine.support.*;

public class ResolvingParametersGenerator implements ParametersGenerator {
	private final Map<Parameter, Shrinkable<Object>> resolvedShrinkables = new HashMap<>();
	private final List<MethodParameter> propertyParameters;
	private final Iterator<List<Shrinkable<Object>>> forAllParametersGenerator;
	private final ResolveParameterHook resolveParameterHook;
	private final PropertyLifecycleContext propertyLifecycleContext;

	public ResolvingParametersGenerator(
		List<MethodParameter> propertyParameters,
		Iterator<List<Shrinkable<Object>>> forAllParametersGenerator,
		ResolveParameterHook resolveParameterHook,
		PropertyLifecycleContext propertyLifecycleContext
	) {
		this.propertyParameters = propertyParameters;
		this.forAllParametersGenerator = forAllParametersGenerator;
		this.resolveParameterHook = resolveParameterHook;
		this.propertyLifecycleContext = propertyLifecycleContext;
	}

	public boolean hasNext() {
		return forAllParametersGenerator.hasNext();
	}

	public List<Shrinkable<Object>> next() {
		List<Shrinkable<Object>> next = new ArrayList<>();
		List<Shrinkable<Object>> forAllShrinkables = new ArrayList<>(forAllParametersGenerator.next());

		for (MethodParameter parameter : propertyParameters) {
			if (parameter.isAnnotated(ForAll.class)) {
				next.add(forAllShrinkables.get(0));
				forAllShrinkables.remove(0);
			} else {
				next.add(findResolvableParameter(parameter));
			}
		}

		return next;
	}

	private Shrinkable<Object> findResolvableParameter(MethodParameter parameter) {
		return resolvedShrinkables.computeIfAbsent(parameter.getRawParameter(), ignore -> resolveShrinkable(parameter));
	}

	private Shrinkable<Object> resolveShrinkable(MethodParameter parameter) {
		ParameterResolutionContext parameterContext = new DefaultParameterInjectionContext(parameter);
		Optional<ParameterSupplier> optionalGenerator = resolveParameterHook.resolve(parameterContext);

		// TODO: Replace with actual tryLifecycleContext from outside:
		LifecycleContext lifecycleContext = new DefaultTryLifecycleContext(propertyLifecycleContext);

		return optionalGenerator.map(generator -> new ShrinkableResolvedParameter(generator, parameterContext, lifecycleContext)).orElseThrow(
			() -> {
				String info = "No matching resolver could be found";
				return new CannotResolveParameterException(parameterContext, info);
			});
	}

}
