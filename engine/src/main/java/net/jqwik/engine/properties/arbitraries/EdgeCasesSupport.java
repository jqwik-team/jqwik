package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.support.JqwikExceptionSupport.*;

public class EdgeCasesSupport {

	public static <T> EdgeCases<T> fromSuppliers(final List<Supplier<Shrinkable<T>>> suppliers) {
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

	public static <T> EdgeCases<T> choose(final List<T> values, int maxEdgeCases) {
		List<Shrinkable<T>> shrinkables = new ArrayList<>();
		if (values.size() > 0) {
			shrinkables.add(new ChooseValueShrinkable<>(values.get(0), values));
		}
		if (values.size() > 1 && (shrinkables.size() < maxEdgeCases)) {
			int lastIndex = values.size() - 1;
			shrinkables.add(new ChooseValueShrinkable<>(values.get(lastIndex), values));
		}
		//noinspection CatchMayIgnoreException
		try {
			if (values.contains(null) && (shrinkables.size() < maxEdgeCases)) {
				shrinkables.add(Shrinkable.unshrinkable(null));
			}
		} catch (NullPointerException someListsDoNotAllowNullValues) { }
		return EdgeCasesSupport.fromShrinkables(shrinkables);
	}

	public static <T> EdgeCases<T> concatFrom(final List<Arbitrary<T>> arbitraries, int maxEdgeCases) {
		List<Shrinkable<Arbitrary<T>>> shrinkables = new ArrayList<>();
		for (Arbitrary<T> arbitrary : arbitraries) {
			shrinkables.add(new ChooseValueShrinkable<>(arbitrary, arbitraries));
		}
		return flatMapArbitrary(fromShrinkables(shrinkables), Function.identity(), maxEdgeCases);
	}

	public static <T> EdgeCases<T> concat(List<EdgeCases<T>> edgeCases, int maxEdgeCases) {
		if (edgeCases.isEmpty() || maxEdgeCases <= 0) {
			return EdgeCases.none();
		}
		if (edgeCases.size() == 1) {
			return edgeCases.get(0);
		}
		List<Supplier<Shrinkable<T>>> concatenatedSuppliers = new ArrayList<>();
		int remainingMaxEdgeCases = maxEdgeCases;
		for (EdgeCases<T> edgeCase : edgeCases) {
			if (edgeCase.isEmpty()) {
				continue;
			}
			List<Supplier<Shrinkable<T>>> suppliers =
				edgeCase.suppliers()
						.stream()
						.limit(Math.max(0, remainingMaxEdgeCases))
						.collect(Collectors.toList());
			concatenatedSuppliers.addAll(suppliers);
			remainingMaxEdgeCases = remainingMaxEdgeCases - suppliers.size();
		}
		return EdgeCasesSupport.fromSuppliers(concatenatedSuppliers);
	}

	public static <T> EdgeCases<T> fromShrinkables(List<Shrinkable<T>> shrinkables) {
		return () -> shrinkables
						 .stream()
						 .map(shrinkable -> (Supplier<Shrinkable<T>>) () -> shrinkable)
						 .collect(Collectors.toList());
	}

	public static <T, U> EdgeCases<U> map(EdgeCases<T> self, Function<T, U> mapper) {
		return mapShrinkable(self, tShrinkable -> tShrinkable.map(mapper));
	}

	public static <T, U> EdgeCases<U> mapShrinkable(EdgeCases<T> self, Function<Shrinkable<T>, Shrinkable<U>> mapper) {
		List<Supplier<Shrinkable<U>>> mappedSuppliers =
			self.suppliers().stream()
				.map(tSupplier -> mapper.apply(tSupplier.get()))
				.filter(Objects::nonNull)
				.map(uShrinkable -> (Supplier<Shrinkable<U>>) () -> uShrinkable)
				.collect(Collectors.toList());
		return EdgeCases.fromSuppliers(mappedSuppliers);
	}

	public static <T> EdgeCases<T> filter(EdgeCases<T> self, Predicate<T> filterPredicate) {
		List<Supplier<Shrinkable<T>>> filteredSuppliers =
			self.suppliers().stream()
				.filter(supplier -> filterPredicate.test(supplier.get().value()))
				.map(supplier -> (Supplier<Shrinkable<T>>) () -> new FilteredShrinkable<>(supplier.get(), filterPredicate))
				.collect(Collectors.toList());
		return EdgeCases.fromSuppliers(filteredSuppliers);
	}

	public static <T> EdgeCases<T> ignoreExceptions(final EdgeCases<T> self, final Class<? extends Throwable>[] exceptionTypes) {
		List<Supplier<Shrinkable<T>>> filteredSuppliers =
			self.suppliers().stream()
				.filter(supplier -> {
					try {
						supplier.get().value();
						return true;
					} catch (Throwable throwable) {
						if (isInstanceOfAny(throwable, exceptionTypes)) {
							return false;
						}
						throw throwable;
					}
				})
				.map(shrinkableSupplier -> (Supplier<Shrinkable<T>>) () -> {
					Shrinkable<T> tShrinkable = shrinkableSupplier.get();
					return new IgnoreExceptionShrinkable<T>(tShrinkable, exceptionTypes);
				})
				.collect(Collectors.toList());
		return EdgeCases.fromSuppliers(filteredSuppliers);
	}

	public static <T> EdgeCases<T> dontShrink(EdgeCases<T> self) {
		return () -> self.suppliers()
						 .stream()
						 .map(supplier -> (Supplier<Shrinkable<T>>) () -> supplier.get().makeUnshrinkable())
						 .collect(Collectors.toList());
	}

	public static <T, U> EdgeCases<U> flatMapArbitrary(
		EdgeCases<T> self,
		Function<T, Arbitrary<U>> mapper,
		int maxEdgeCases
	) {
		List<Supplier<Shrinkable<U>>> flatMappedSuppliers =
			self.suppliers().stream()
				.flatMap(tSupplier -> {
					T t = tSupplier.get().value();
					return mapper.apply(t).edgeCases(maxEdgeCases).suppliers()
								 .stream()
								 .map(uSupplier -> {
									 Function<T, Shrinkable<U>> shrinkableMapper =
										 newT -> mapper.apply(newT).generator(1000)
													   .next(SourceOfRandomness.newRandom(42L));
									 return (Supplier<Shrinkable<U>>) () -> new FixedValueFlatMappedShrinkable<>(
										 tSupplier.get(),
										 shrinkableMapper,
										 uSupplier
									 );
								 });
				})
				.limit(Math.max(0, maxEdgeCases))
				.collect(Collectors.toList());
		return EdgeCases.fromSuppliers(flatMappedSuppliers);
	}

	public static <T> EdgeCases<T> combine(
		final List<Arbitrary<Object>> arbitraries,
		final Function<List<Object>, T> combineFunction,
		int maxEdgeCases
	) {
		if (arbitraries.isEmpty() || maxEdgeCases <= 0) {
			return EdgeCases.none();
		}
		List<Iterable<Supplier<Shrinkable<Object>>>> listOfSuppliers = new ArrayList<>();
		int remainingEdgeCases = maxEdgeCases;
		for (Arbitrary<Object> a : arbitraries) {
			EdgeCases<Object> edgeCases = a.edgeCases(remainingEdgeCases);
			if (edgeCases.isEmpty()) {
				return EdgeCases.none();
			}
			List<Supplier<Shrinkable<Object>>> supplierList = edgeCases.suppliers();
			listOfSuppliers.add(supplierList);
			remainingEdgeCases = (int) Math.max(1, Math.ceil(remainingEdgeCases / (double) supplierList.size()));
		}

		Iterator<List<Supplier<Shrinkable<Object>>>> iterator = Combinatorics.combine(listOfSuppliers);

		List<Supplier<Shrinkable<T>>> suppliers = new ArrayList<>();
		int count = 0;
		while (iterator.hasNext() && count < maxEdgeCases) {
			List<Supplier<Shrinkable<Object>>> next = iterator.next();
			List<Shrinkable<Object>> shrinkables = next.stream().map(Supplier::get).collect(Collectors.toList());
			suppliers.add(() -> new CombinedShrinkable<>(shrinkables, combineFunction));
			count++;
		}

		return EdgeCases.fromSuppliers(suppliers);
	}

}
