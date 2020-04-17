package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.3.0")
public interface EdgeCases<T> extends Iterable<Shrinkable<T>> {

	boolean isEmpty();

	@API(status = INTERNAL)
	static <T> EdgeCases<T> none() {
		Iterable<Shrinkable<T>> iterable = Collections::emptyIterator;
		return fromIterable(true, iterable);
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> fromIterable(boolean empty, Iterable<Shrinkable<T>> iterable) {
		return new EdgeCases<T>() {
			@Override
			public boolean isEmpty() {
				return empty;
			}

			@Override
			public Iterator<Shrinkable<T>> iterator() {
				return iterable.iterator();
			}
		};
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> fromSupplier(Supplier<Shrinkable<T>> supplier) {
		return fromIterable(false, () -> Stream.of(supplier.get()).iterator());
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> concat(EdgeCases<T> first, EdgeCases<T> second) {
		if (first.isEmpty()) {
			if (second.isEmpty()) {
				return none();
			} else {
				return second;
			}
		} else {
			if (second.isEmpty()) {
				return first;
			} else {
				return fromIterable(false, () -> Stream.concat(first.stream(), second.stream()).iterator());
			}
		}
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
		if (suppliers.isEmpty()) {
			return none();
		}
		return fromIterable(false, () -> suppliers.stream().map(Supplier::get).iterator());
	}

	@API(status = INTERNAL)
	default <U> EdgeCases<U> map(Function<T, U> mapper) {
		if (isEmpty()) {
			return none();
		}
		return fromIterable(false, () -> {
			Stream<Shrinkable<T>> stream = stream();
			return stream.map(shrinkable -> shrinkable.map(mapper)).iterator();
		});
	}

	@API(status = INTERNAL)
	default Stream<Shrinkable<T>> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}

	@API(status = INTERNAL)
	default <U> EdgeCases<U> mapShrinkable(Function<Shrinkable<T>, Shrinkable<U>> mapper) {
		if (isEmpty()) {
			return none();
		}
		return fromIterable(false, () -> {
			Stream<Shrinkable<T>> stream = EdgeCases.this.stream();
			return stream.map(mapper).iterator();
		});
	}

}
