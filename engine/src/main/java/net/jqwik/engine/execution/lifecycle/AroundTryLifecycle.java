package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;

public class AroundTryLifecycle implements CheckedFunction {

	private final TryExecutor tryExecutor;
	private final AroundTryHook aroundTry;
	private final TryLifecycleContextForMethod tryLifecycleContext;

	public AroundTryLifecycle(
		CheckedFunction rawFunction,
		PropertyLifecycleContext propertyLifecycleContext,
		AroundTryHook aroundTry
	) {
		this.tryExecutor = rawFunction::test;
		this.tryLifecycleContext = new TryLifecycleContextForMethod(propertyLifecycleContext);
		this.aroundTry = aroundTry;
	}

	@Override
	public boolean test(List<Object> parameters) {
		try {
			return aroundTry.aroundTry(tryLifecycleContext, tryExecutor, parameters);
		} catch (Throwable throwable) {
			//noinspection ConstantConditions
			return JqwikExceptionSupport.throwAsUncheckedException(throwable);
		}
	}
}
