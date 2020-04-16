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
		return () -> Stream.concat(first.toStream(), second.toStream()).iterator();
	}

	static <T> EdgeCases<T> fromSuppliers(Set<Supplier<Shrinkable<T>>> suppliers) {
		return () -> suppliers.stream().map(Supplier::get).iterator();
	}

	@API(status = INTERNAL)
	default <U> EdgeCases<U> map(Function<T, U> mapper) {
		return () -> {
			Stream<Shrinkable<T>> stream = toStream();
			return stream.map(shrinkable -> shrinkable.map(mapper)).iterator();
		};
	}

	@API(status = INTERNAL)
	default Stream<Shrinkable<T>> toStream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	default <U> EdgeCases<U> mapShrinkable(Function<Shrinkable<T>, Shrinkable<U>> mapper) {
		return () -> {
			Stream<Shrinkable<T>> stream = EdgeCases.this.toStream();
			return stream.map(mapper).iterator();
		};
	}

}
