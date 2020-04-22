package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.*;

/**
 * Is loaded through reflection in api module
 */
public class EdgeCasesFacadeImpl extends EdgeCases.EdgeCasesFacade {

	@Override
	public <T> EdgeCases<T> concat(List<EdgeCases<T>> edgeCases) {
		if (edgeCases.isEmpty()) {
			return EdgeCases.none();
		}
		// TODO: Filter out duplicate edge cases
		List<Supplier<Shrinkable<T>>> concatenatedSuppliers = new ArrayList<>();
		for (EdgeCases<T> edgeCase : edgeCases) {
			if (edgeCase.isEmpty()) {
				continue;
			}
			concatenatedSuppliers.addAll(edgeCase.suppliers());
		}
		return EdgeCases.fromSuppliers(concatenatedSuppliers);
	}

	@Override
	public <T, U> EdgeCases<U> mapShrinkable(EdgeCases<T> self, Function<Shrinkable<T>, Shrinkable<U>> mapper) {
		List<Supplier<Shrinkable<U>>> mappedSuppliers =
			self.suppliers().stream()
				.map(tSupplier -> (Supplier<Shrinkable<U>>) () -> mapper.apply(tSupplier.get()))
				.collect(Collectors.toList());
		return EdgeCases.fromSuppliers(mappedSuppliers);
	}

	@Override
	public <T, U> EdgeCases<U> flatMap(EdgeCases<T> self, Function<T, EdgeCases<U>> mapper) {
		List<Supplier<Shrinkable<U>>> flatMappedSuppliers =
			self.suppliers().stream()
				.flatMap(tSupplier -> {
					T t = tSupplier.get().value();
					return mapper.apply(t).suppliers()
								 .stream()
								 .map(uSupplier -> {
									 Function<T, Shrinkable<U>> shrinkableMapper = ignoreT -> uSupplier.get();
									 return (Supplier<Shrinkable<U>>) () -> new FlatMappedShrinkable<>(tSupplier.get(), shrinkableMapper);
								 });
				})
				// .map(supplier -> (Supplier<Shrinkable<U>>) () -> {
				// })
				.collect(Collectors.toList());
		return EdgeCases.fromSuppliers(flatMappedSuppliers);
	}

	@Override
	public <T> EdgeCases<T> filter(EdgeCases<T> self, Predicate<T> filterPredicate) {
		List<Supplier<Shrinkable<T>>> filteredSuppliers =
			self.suppliers().stream()
				.filter(supplier -> filterPredicate.test(supplier.get().value()))
				.map(supplier -> (Supplier<Shrinkable<T>>) () -> new FilteredShrinkable<>(supplier.get(), filterPredicate))
				.collect(Collectors.toList());
		return EdgeCases.fromSuppliers(filteredSuppliers);
	}
}
