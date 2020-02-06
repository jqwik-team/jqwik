package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import org.opentest4j.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;

// TODO: Get rid of this ridiculous wrapping / unwrapping of CheckedFunction
public class AroundTryLifecycle implements CheckedFunction {

	private final TryExecutor tryExecutor;
	private final AroundTryHook aroundTry;
	private final TryLifecycleContextForMethod tryLifecycleContext;

	public AroundTryLifecycle(
		CheckedFunction rawFunction,
		PropertyLifecycleContext propertyLifecycleContext,
		AroundTryHook aroundTry
	) {
		this.tryExecutor = createExecutor(rawFunction);
		this.tryLifecycleContext = new TryLifecycleContextForMethod(propertyLifecycleContext);
		this.aroundTry = aroundTry;
	}

	private TryExecutor createExecutor(CheckedFunction rawFunction) {
		return parameters ->  {
			try {
				boolean result = rawFunction.test(parameters);
				return result ? TryExecutionResult.satisfied() : TryExecutionResult.falsified(null);
			} catch (TestAbortedException tea) {
				return TryExecutionResult.invalid();
			} catch (AssertionError|Exception e) {
				return TryExecutionResult.falsified(e);
			} catch (Throwable throwable) {
				JqwikExceptionSupport.rethrowIfBlacklisted(throwable);
				return JqwikExceptionSupport.throwAsUncheckedException(throwable);
			}
		};
	}

	@Override
	public boolean test(List<Object> parameters) {
		try {
			TryExecutionResult result = aroundTry.aroundTry(tryLifecycleContext, tryExecutor, parameters);
			switch (result.status()) {
				case INVALID:
					throw new TestAbortedException();
				case FALSIFIED:
					return false;
				default:
					return true;
			}
		} catch (Throwable throwable) {
			//noinspection ConstantConditions
			return JqwikExceptionSupport.throwAsUncheckedException(throwable);
		}
	}
}
