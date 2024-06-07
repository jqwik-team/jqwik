package net.jqwik.api;

import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.lifecycle.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * This interface is no longer used anywhere in jqwik's public API
 * and could therefore be deprecated and moved to engine implementation.
 * But since it's declared to be stable it won't go away before version 2.0.
 *
 * @param <T> The type of
 */
@FunctionalInterface
@API(status = STABLE, since = "1.0")
public interface Falsifier<T extends @Nullable Object> {

	@API(status = INTERNAL)
	TryExecutionResult execute(T t);

	@API(status = INTERNAL)
	default Falsifier<T> withFilter(Predicate<? super T> filter) {
		return value -> {
			if (!filter.test(value)) {
				return TryExecutionResult.invalid();
			}
			return execute(value);
		};
	}

}
