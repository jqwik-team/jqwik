package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

public class AroundTryLifecycle implements CheckedFunction {

	private final CheckedFunction rawFunction;
	private final PropertyLifecycleContext propertyLifecycleContext;
	private final AroundTryHook aroundTry;

	public AroundTryLifecycle(
		CheckedFunction rawFunction,
		PropertyLifecycleContext propertyLifecycleContext,
		AroundTryHook aroundTry
	) {
		this.rawFunction = rawFunction;
		this.propertyLifecycleContext = propertyLifecycleContext;
		this.aroundTry = aroundTry;
	}

	@Override
	public boolean test(List<Object> params) {
		// TODO: Add AroundTry lifecycle
		return rawFunction.test(params);
	}
}
