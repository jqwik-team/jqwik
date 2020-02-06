package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.support.*;

public class AroundTryLifecycle implements TryExecutor {

	private final TryExecutor tryExecutor;
	private final AroundTryHook aroundTry;
	private final TryLifecycleContextForMethod tryLifecycleContext;

	public AroundTryLifecycle(
		TryExecutor tryExecutor,
		PropertyLifecycleContext propertyLifecycleContext,
		AroundTryHook aroundTry
	) {
		this.tryExecutor = tryExecutor;
		this.tryLifecycleContext = new TryLifecycleContextForMethod(propertyLifecycleContext);
		this.aroundTry = aroundTry;
	}

	@Override
	public TryExecutionResult execute(List<Object> parameters) {
		try {
			return aroundTry.aroundTry(tryLifecycleContext, tryExecutor, parameters);
		} catch (Throwable throwable) {
			return JqwikExceptionSupport.throwAsUncheckedException(throwable);
		}
	}

}
