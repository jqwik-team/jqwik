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

	@SafeVarargs
	@API(status = INTERNAL)
	static <T> EdgeCases<T> concat(EdgeCases<T>... rest) {
		return concat(Arrays.asList(rest));
	}

	@API(status = INTERNAL)
	static <T> EdgeCases<T> concat(List<EdgeCases<T>> edgeCases) {
		if (edgeCases.isEmpty()) {
			return none();
		}
		// TODO: Filter out duplicate edge cases
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

	/**
	 * Only use for immutable values
	 */
	@API(status = INTERNAL)
	static <T> EdgeCases<T> fromShrinkables(List<Shrinkable<T>> shrinkables) {
		return () -> shrinkables
						 .stream()
						 .map(shrinkable -> (Supplier<Shrinkable<T>>) () -> shrinkable)
						 .collect(Collectors.toList());
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

	@API(status = INTERNAL)
	default EdgeCases<T> filter(Predicate<T> filterPredicate) {
		List<Supplier<Shrinkable<T>>> filteredSuppliers =
			suppliers().stream()
					   .filter(supplier -> filterPredicate.test(supplier.get().value()))
					   // .map(supplier -> t -> )
					   .collect(Collectors.toList());
		return fromSuppliers(filteredSuppliers);
	}

	@API(status = INTERNAL)
	default <U> EdgeCases<U> flatMap(Function<T, EdgeCases<U>> mapper) {
		List<Supplier<Shrinkable<U>>> flatMappedSuppliers =
			suppliers().stream()
					   .flatMap(supplier -> mapper.apply(supplier.get().value()).suppliers().stream())
					   .collect(Collectors.toList());
		return fromSuppliers(flatMappedSuppliers);
	}
}
