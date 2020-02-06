package net.jqwik.api;

import java.util.function.*;

import org.apiguardian.api.*;
import org.opentest4j.*;

import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;

@FunctionalInterface
@API(status = STABLE, since = "1.0")
public interface Falsifier<T> {

	TryExecutionResult executeTry(T t);

	@API(status = INTERNAL)
	default FalsificationResult<T> falsify(Shrinkable<T> candidate) {
		try {
			TryExecutionResult executionResult = executeTry(candidate.value());
			switch (executionResult.status()) {
				case FALSIFIED:
					return FalsificationResult.falsified(candidate, executionResult.throwable().orElse(null));
				case INVALID:
					return FalsificationResult.filtered(candidate);
				default:
					return FalsificationResult.notFalsified(candidate);
			}
		} catch (Throwable throwable) {
			return FalsificationResult.falsified(candidate, throwable);
		}
	}

	@API(status = INTERNAL)
	default Falsifier<T> withFilter(Predicate<T> filter) {
		return value -> {
			if (!filter.test(value)) {
				return TryExecutionResult.invalid();
			}
			return executeTry(value);
		};
	}

	@API(status = INTERNAL)
	default Falsifier<T> withPostFilter(Predicate<T> filter) {
		return value -> {
			try {
				return Falsifier.this.executeTry(value);
			} finally {
				if (!filter.test(value)) {
					return TryExecutionResult.invalid();
				}
			}
		};
	}

}
