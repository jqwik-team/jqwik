package net.jqwik.engine.properties.shrinking;

import java.util.function.*;

import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

public interface TestingFalsifier<T> extends Falsifier<T>, Predicate<T> {

	@Override
	default TryExecutionResult execute(T parameters) {
		try {
			boolean result = this.test(parameters);
			return result ? TryExecutionResult.satisfied() : TryExecutionResult.falsified(null);
		} catch (TestAbortedException tea) {
			return TryExecutionResult.invalid();
		} catch (AssertionError | Exception e) {
			return TryExecutionResult.falsified(e);
		} catch (Throwable throwable) {
			return JqwikExceptionSupport.throwAsUncheckedException(throwable);
		}
	}

}
