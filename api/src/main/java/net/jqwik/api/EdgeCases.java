package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.3.0")
public interface EdgeCases<T> extends Iterable<Shrinkable<T>> {

	List<Supplier<Shrinkable<T>>> suppliers();

	default int size() {
		return suppliers().size();
	}

	default boolean isEmpty() {
		return size() == 0;
	}

	default Iterator<Shrinkable<T>> iterator() {
		return suppliers().stream().map(Supplier::get).iterator();
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> none() {
		return fromSuppliers(Collections.emptyList());
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> fromSupplier(Supplier<Shrinkable<T>> supplier) {
		return fromSuppliers(Collections.singletonList(supplier));
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> concat(EdgeCases<T> first, EdgeCases<T> second) {
		List<EdgeCases<T>> edgeCases = new ArrayList<>();
		edgeCases.add(first);
		edgeCases.add(second);
		return concat(edgeCases);
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> concat(List<EdgeCases<T>> edgeCases) {
		if (edgeCases.isEmpty()) {
			return none();
		}
		List<Supplier<Shrinkable<T>>> concatenatedSuppliers = new ArrayList<>();
		for (EdgeCases<T> edgeCase : edgeCases) {
			if (edgeCase.isEmpty()) {
				continue;
			}
			concatenatedSuppliers.addAll(edgeCase.suppliers());
		}
		return fromSuppliers(concatenatedSuppliers);
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> fromSuppliers(List<Supplier<Shrinkable<T>>> suppliers) {
		return () -> suppliers;
	}

	@API(status = INTERNAL)
	default <U> EdgeCases<U> map(Function<T, U> mapper) {
		return mapShrinkable(tShrinkable -> tShrinkable.map(mapper));
	}

	@API(status = INTERNAL)
	default <U> EdgeCases<U> mapShrinkable(Function<Shrinkable<T>, Shrinkable<U>> mapper) {
		List<Supplier<Shrinkable<U>>> mappedSuppliers =
			suppliers().stream()
					   .map(tSupplier -> (Supplier<Shrinkable<U>>) () -> mapper.apply(tSupplier.get()))
					   .collect(Collectors.toList());
		return fromSuppliers(mappedSuppliers);
	}
}
