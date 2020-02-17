package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import org.opentest4j.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.support.*;

public class AroundTryLifecycle implements TryExecutor {

	private final TryExecutor tryExecutor;
	private final AroundTryHook aroundTry;
	private final TryLifecycleContext tryLifecycleContext;

	public AroundTryLifecycle(
		TryExecutor tryExecutor,
		PropertyLifecycleContext propertyLifecycleContext,
		AroundTryHook aroundTry
	) {
		this.tryExecutor = tryExecutor;
		this.tryLifecycleContext = new DefaultTryLifecycleContext(propertyLifecycleContext);
		this.aroundTry = aroundTry;
	}

	@Override
	public TryExecutionResult execute(List<Object> parameters) {
		// TODO: Remove duplication with CheckedFunction.execute()
		try {
			return aroundTry.aroundTry(tryLifecycleContext, tryExecutor, parameters);
		} catch (TestAbortedException exception) {
			return TryExecutionResult.invalid();
		} catch (AssertionError | Exception e) {
			return TryExecutionResult.falsified(e);
		} catch (Throwable throwable) {
			return JqwikExceptionSupport.throwAsUncheckedException(throwable);
		}
	}

}
