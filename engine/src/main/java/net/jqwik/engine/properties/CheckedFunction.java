package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;

import org.opentest4j.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.support.*;

public interface CheckedFunction extends Predicate<List<Object>> {

	static CheckedFunction fromTryExecutor(TryExecutor tryExecutor) {
		return parameters -> {
			try {
				TryExecutionResult result = tryExecutor.execute(parameters);
				switch (result.status()) {
					case INVALID:
						throw new TestAbortedException();
					case FALSIFIED:
						if (result.throwable().isPresent()) {
							throw result.throwable().get();
						}
						return false;
					default:
						return true;
				}
			} catch (Throwable throwable) {
				//noinspection ConstantConditions
				return JqwikExceptionSupport.throwAsUncheckedException(throwable);
			}
		};
	}

	default TryExecutor asTryExecutor() {
		return parameters ->  {
			try {
				boolean result = this.test(parameters);
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


}
