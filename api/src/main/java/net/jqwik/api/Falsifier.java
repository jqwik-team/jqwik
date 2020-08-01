package net.jqwik.api;

import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;

@FunctionalInterface
@API(status = STABLE, since = "1.0")
public interface Falsifier<T> {

	@API(status = INTERNAL)
	TryExecutionResult execute(T t);

	@API(status = INTERNAL)
	default FalsificationResult<T> falsify(Shrinkable<T> candidate) {
		try {
			TryExecutionResult executionResult = execute(candidate.value());
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
			return execute(value);
		};
	}

	@API(status = INTERNAL)
	default <U> Falsifier<U> map(Function<U, T> mapper) {
		return value -> {
			T other = null;
			try {
				other = mapper.apply(value);
			} catch (Throwable throwable) {
				// Ignore exceptions during shrinking
				if (throwable instanceof OutOfMemoryError) {
					throw throwable;
				}
				return TryExecutionResult.invalid();
			}
			return Falsifier.this.execute(other);
		};
	}

}
