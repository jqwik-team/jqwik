package net.jqwik.engine.execution;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public class ShrinkableResolvedParameter implements Shrinkable<Object> {
	private final ResolveParameterHook.ParameterSupplier supplier;
	private final ParameterResolutionContext context;
	private final TryLifecycleContext tryLifecycleContext;

	public ShrinkableResolvedParameter(
		ResolveParameterHook.ParameterSupplier supplier,
		ParameterResolutionContext context,
		TryLifecycleContext tryLifecycleContext
	) {
		this.supplier = supplier;
		this.context = context;
		this.tryLifecycleContext = tryLifecycleContext;
	}

	@Override
	public Object value() {
		Optional<TryLifecycleContext> optionalTry = Optional.of(tryLifecycleContext);
		Object value = supplier.get(optionalTry);
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
	public Stream<Shrinkable<Object>> shrink() {
		return Stream.empty();
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.MIN;
	}

	@Override
	public String toString() {
		return String.format("Unshrinkable resolved parameter for [%s]", context.parameter());
	}

}
