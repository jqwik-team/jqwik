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
	default Falsifier<T> withFilter(Predicate<T> filter) {
		return value -> {
			if (!filter.test(value)) {
				return TryExecutionResult.invalid();
			}
			return execute(value);
		};
	}

}
