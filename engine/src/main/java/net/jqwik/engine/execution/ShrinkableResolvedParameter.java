package net.jqwik.engine.execution;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public class ShrinkableResolvedParameter implements Shrinkable<Object> {
	private final ResolveParameterHook.ParameterSupplier supplier;
	private final ParameterResolutionContext context;
	private LifecycleContext lifecycleContext;

	public ShrinkableResolvedParameter(
		ResolveParameterHook.ParameterSupplier supplier,
		ParameterResolutionContext context,
		LifecycleContext lifecycleContext
	) {
		this.supplier = supplier;
		this.context = context;
		this.lifecycleContext = lifecycleContext;
	}

	@Override
	public Object value() {
		Object value = supplier.get(lifecycleContext);
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
