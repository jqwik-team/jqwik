package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

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

	public static <T> EdgeCases<T> choose(final List<T> values) {
		List<Shrinkable<T>> shrinkables = new ArrayList<>();
		if (values.size() > 0) {
			shrinkables.add(new ChooseValueShrinkable<>(values.get(0), values));
		}
		if (values.size() > 1) {
			int lastIndex = values.size() - 1;
			shrinkables.add(new ChooseValueShrinkable<>(values.get(lastIndex), values));
		}
		//noinspection CatchMayIgnoreException
		try {
			if (values.contains(null)) {
				shrinkables.add(Shrinkable.unshrinkable(null));
			}
		} catch (NullPointerException someListsDoNotAllowNullValues) { }
		return EdgeCasesSupport.fromShrinkables(shrinkables);
	}

	public static <T> EdgeCases<T> concatFrom(final List<Arbitrary<T>> arbitraries) {
		List<Shrinkable<Arbitrary<T>>> shrinkables = new ArrayList<>();
		for (Arbitrary<T> arbitrary : arbitraries) {
			shrinkables.add(new ChooseValueShrinkable<>(arbitrary, arbitraries));
		}
		return flatMapArbitrary(fromShrinkables(shrinkables), Function.identity());
	}

	public static <T> EdgeCases<T> concat(List<EdgeCases<T>> edgeCases) {
		if (edgeCases.isEmpty()) {
			return EdgeCases.none();
		}
		if (edgeCases.size() == 1) {
			return edgeCases.get(0);
		}
		List<Supplier<Shrinkable<T>>> concatenatedSuppliers = new ArrayList<>();
		for (EdgeCases<T> edgeCase : edgeCases) {
			if (edgeCase.isEmpty()) {
				continue;
			}
			concatenatedSuppliers.addAll(edgeCase.suppliers());
		}
		return EdgeCasesSupport.fromSuppliers(concatenatedSuppliers);
	}

	@SafeVarargs
	static <T> EdgeCases<T> concat(EdgeCases<T>... rest) {
		return EdgeCasesSupport.concat(Arrays.asList(rest));
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

	public static <T> EdgeCases<T> ignoreException(final EdgeCases<T> self, final Class<? extends Throwable> exceptionType) {
		List<Supplier<Shrinkable<T>>> filteredSuppliers =
				self.suppliers().stream()
					.filter(supplier -> {
						try {
							supplier.get().value();
							return true;
						} catch (Throwable throwable) {
							if (exceptionType.isAssignableFrom(throwable.getClass())) {
								return false;
							}
							throw throwable;
						}
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

	public static <T, U> EdgeCases<U> flatMapArbitrary(EdgeCases<T> self, Function<T, Arbitrary<U>> mapper) {
		List<Supplier<Shrinkable<U>>> flatMappedSuppliers =
				self.suppliers().stream()
					.flatMap(tSupplier -> {
						T t = tSupplier.get().value();
						return mapper.apply(t).edgeCases().suppliers()
									 .stream()
									 .map(uSupplier -> {
										 Function<T, Shrinkable<U>> shrinkableMapper =
												 newT -> mapper.apply(newT).generator(1000).next(SourceOfRandomness.newRandom(42L));
										 return (Supplier<Shrinkable<U>>) () -> new FixedValueFlatMappedShrinkable<>(
												 tSupplier.get(),
												 shrinkableMapper,
												 uSupplier
										 );
									 });
					})
					.limit(MAX_NUMBER_OF_EDGE_CASES)
					.collect(Collectors.toList());
		if (flatMappedSuppliers.size() >= MAX_NUMBER_OF_EDGE_CASES) {
			logTooManyEdgeCases(MAX_NUMBER_OF_EDGE_CASES);
		}
		return EdgeCases.fromSuppliers(flatMappedSuppliers);
	}

	private static final int MAX_NUMBER_OF_EDGE_CASES = 1000;

	private static final Logger LOG = Logger.getLogger(EdgeCasesSupport.class.getName());

	private static final Store<Boolean> warningAlreadyLogged;

	// TODO: Get rid of after refactoring edge case generation
	static {
		warningAlreadyLogged = initWarningAlreadyLogged();
	}

	private static Store<Boolean> initWarningAlreadyLogged() {
		try {
			return Store.create(
					Tuple.of(EdgeCasesFacadeImpl.class, "warning"),
					Lifespan.PROPERTY,
					() -> false
			);
		} catch (JqwikException jqwikException) {
			// this can happen if arbitraries are used outside a Jqwik context
			// TODO: Get rid of that store which is a hack anyway
			return new Store<Boolean>() {
				@Override
				public Boolean get() {
					return true;
				}

				@Override
				public Lifespan lifespan() {
					return Lifespan.PROPERTY;
				}

				@Override
				public void update(Function<Boolean, Boolean> updater) {

				}

				@Override
				public void reset() {

				}

				@Override
				public Store<Boolean> onClose(Consumer<Boolean> onCloseCallback) {
					return this;
				}
			};
		}
	}

	private static void logTooManyEdgeCases(int maxNumberOfEdgeCases) {
		if (warningAlreadyLogged.get()) {
			// This is a terrible hack to suppress multiple logging
			// TODO: Remove by properly implementing generation of edge cases
			return;
		}
		String message = String.format(
				"Combinatorial explosion of edge case generation. Stopped creating more after %s generated cases.",
				maxNumberOfEdgeCases
		);
		LOG.warning(message);
		warningAlreadyLogged.update(ignore -> true);
	}

}
