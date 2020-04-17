package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.3.0")
public interface EdgeCases<T> extends Iterable<Shrinkable<T>> {

	@API(status = INTERNAL)
	static <T> EdgeCases<T> none() {
		return Collections::emptyIterator;
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> fromSupplier(Supplier<Shrinkable<T>> supplier) {
		return () -> Stream.of(supplier.get()).iterator();
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> concat(EdgeCases<T> first, EdgeCases<T> second) {
		return () -> Stream.concat(first.stream(), second.stream()).iterator();
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> concat(List<EdgeCases<T>> edgeCases) {
		if (edgeCases.isEmpty()) {
			throw new IllegalArgumentException("There must be at least one edgeCases object");
		}
		EdgeCases<T> result = null;
		for (EdgeCases<T> edgeCase : edgeCases) {
			if (result == null) {
				result = edgeCase;
			} else {
				result = EdgeCases.concat(result, edgeCase);
			}
		}
		return result;
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> fromSuppliers(List<Supplier<Shrinkable<T>>> suppliers) {
		return () -> suppliers.stream().map(Supplier::get).iterator();
	}

	@API(status = INTERNAL)
	default <U> EdgeCases<U> map(Function<T, U> mapper) {
		return () -> {
			Stream<Shrinkable<T>> stream = stream();
			return stream.map(shrinkable -> shrinkable.map(mapper)).iterator();
		};
	}

	@API(status = INTERNAL)
	default Stream<Shrinkable<T>> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	@API(status = INTERNAL)
	default <U> EdgeCases<U> mapShrinkable(Function<Shrinkable<T>, Shrinkable<U>> mapper) {
		return () -> {
			Stream<Shrinkable<T>> stream = EdgeCases.this.stream();
			return stream.map(mapper).iterator();
		};
	}

}
