package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

class AroundTryLifecycle implements CheckedFunction {

	private final CheckedFunction rawFunction;
	private final PropertyLifecycleContext propertyLifecycleContext;

	public AroundTryLifecycle(CheckedFunction rawFunction, PropertyLifecycleContext propertyLifecycleContext) {
		this.rawFunction = rawFunction;
		this.propertyLifecycleContext = propertyLifecycleContext;
	}

	@Override
	public boolean test(List<Object> params) {
		// TODO: Add AroundTry lifecycle
		return rawFunction.test(params);
	}
}
