package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;

import org.opentest4j.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.execution.lifecycle.*;

@FunctionalInterface
public interface CheckedFunction extends Predicate<List<Object>>, TryExecutor, TryLifecycleExecutor {

	@Override
	default TryExecutionResult execute(List<Object> parameters) {
		try {
			boolean result = this.test(parameters);
			return result ? TryExecutionResult.satisfied() : TryExecutionResult.falsified(null);
		} catch (TestAbortedException tea) {
			return TryExecutionResult.invalid();
		} catch (AssertionError | Exception e) {
			return TryExecutionResult.falsified(e);
		}
	}

	/**
	 * Only needed to simplify some tests
	 */
	@Override
	default TryExecutionResult execute(TryLifecycleContext tryLifecycleContext, List<Object> parameters) {
		return execute(parameters);
	}
}
