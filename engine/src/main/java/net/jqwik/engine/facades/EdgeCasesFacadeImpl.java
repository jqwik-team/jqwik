package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

/**
 * Is loaded through reflection in api module
 */
public class EdgeCasesFacadeImpl extends EdgeCases.EdgeCasesFacade {

	@Override
	public <T> EdgeCases<T> fromSuppliers(final List<Supplier<Shrinkable<T>>> suppliers) {
		return new EdgeCases<T>() {
			@Override
			public List<Supplier<Shrinkable<T>>> suppliers() {
				return suppliers;
			}

			@Override
			public String toString() {
				String edgeCases =
					suppliers
						.stream()
						.map(Supplier::get)
						.map(Shrinkable::value)
						.map(JqwikStringSupport::displayString)
						.collect(Collectors.joining(", "));
				return String.format("EdgeCases[%s]", edgeCases);
			}
		};
	}

	@Override
	public <T> EdgeCases<T> concat(List<EdgeCases<T>> edgeCases) {
		if (edgeCases.isEmpty()) {
			return EdgeCases.none();
		}
		// TODO: Should duplicate edge cases be filtered out?
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
	public <T, U> EdgeCases<U> flatMapArbitrary(EdgeCases<T> self, Function<T, Arbitrary<U>> mapper) {
		List<Supplier<Shrinkable<U>>> flatMappedSuppliers =
			self.suppliers().stream()
				.flatMap(tSupplier -> {
					T t = tSupplier.get().value();
					return mapper.apply(t).edgeCases().suppliers()
								 .stream()
								 .map(uSupplier -> {
									 Function<T, Shrinkable<U>> shrinkableMapper =
										 newT -> mapper.apply(newT).generator(1000).next(new Random(42L));
									 return (Supplier<Shrinkable<U>>) () -> new FlatMappedShrinkable<>(
									 	tSupplier.get(),
										uSupplier.get(),
										shrinkableMapper
									 );
								 });
				})
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
