package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

public class ResolvingParametersGenerator implements Iterator<List<Shrinkable<Object>>> {
	private final Map<Parameter, Shrinkable<Object>> resolvedShrinkables = new HashMap<>();
	private final List<MethodParameter> propertyParameters;
	private final Iterator<List<Shrinkable<Object>>> forAllParametersGenerator;
	private final ResolveParameterHook injectParameterHook;
	private final PropertyLifecycleContext propertyLifecycleContext;

	public ResolvingParametersGenerator(
		List<MethodParameter> propertyParameters,
		Iterator<List<Shrinkable<Object>>> forAllParametersGenerator,
		ResolveParameterHook injectParameterHook,
		PropertyLifecycleContext propertyLifecycleContext
	) {
		this.propertyParameters = propertyParameters;
		this.forAllParametersGenerator = forAllParametersGenerator;
		this.injectParameterHook = injectParameterHook;
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
				next.add(findInjectableParameter(parameter));
			}
		}

		return next;
	}

	private Shrinkable<Object> findInjectableParameter(MethodParameter parameter) {
		return resolvedShrinkables.computeIfAbsent(parameter.getRawParameter(), ignore -> resolveShrinkable(parameter));
	}

	private Shrinkable<Object> resolveShrinkable(MethodParameter parameter) {
		ParameterResolutionContext parameterContext = new DefaultParameterInjectionContext(parameter);
		Optional<Supplier<Object>> optionalSupplier = injectParameterHook.resolve(parameterContext, propertyLifecycleContext);
		return optionalSupplier.map(supplier -> createShrinkable(supplier, parameterContext)).orElseThrow(
			() -> {
				String message = String.format("Parameter [%s] without @ForAll cannot be resolved", parameter);
				return new JqwikException(message);
			});
	}

	private Shrinkable<Object> createShrinkable(Supplier<Object> supplier, ParameterResolutionContext context) {
		return new Shrinkable<Object>() {
			@Override
			public Object value() {
				return supplier.get();
			}

			@Override
			public ShrinkingSequence<Object> shrink(Falsifier<Object> falsifier) {
				return ShrinkingSequence.dontShrink(this);
			}

			@Override
			public ShrinkingDistance distance() {
				return ShrinkingDistance.of(0);
			}

			@Override
			public String toString() {
				return String.format("Unshrinkable injected parameter for [%s]", context.parameter());
			}
		};
	}
}
