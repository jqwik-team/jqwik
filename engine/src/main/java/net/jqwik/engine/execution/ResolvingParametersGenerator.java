package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
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
		Optional<Supplier<Object>> optionalSupplier = resolveParameterHook.resolve(parameterContext, propertyLifecycleContext);
		return optionalSupplier.map(supplier -> new ShrinkableResolvedParameter(supplier, parameterContext)).orElseThrow(
			() -> {
				String info = "No matching resolver could be found";
				return new CannotResolveParameterException(parameterContext, info);
			});
	}

	private static class ShrinkableResolvedParameter implements Shrinkable<Object> {
		private Supplier<Object> supplier;
		private ParameterResolutionContext context;

		public ShrinkableResolvedParameter(Supplier<Object> supplier, ParameterResolutionContext context) {
			this.supplier = supplier;
			this.context = context;
		}

		@Override
		public Object value() {
			Object value = supplier.get();
			if (!context.typeUsage().isAssignableFrom(value.getClass())) {
				String info = String.format(
					"Type [%s] of resolved value does not fit parameter type [%s]",
					value.getClass().getName(),
					context.parameter().getParameterizedType().getTypeName()
				);
				throw new CannotResolveParameterException(context, info);
			}
			return value;
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
			return String.format("Unshrinkable resolved parameter for [%s]", context.parameter());
		}

	}
}
