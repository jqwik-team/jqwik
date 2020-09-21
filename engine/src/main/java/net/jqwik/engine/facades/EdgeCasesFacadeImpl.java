package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

/**
 * Is loaded through reflection in api module
 */
public class EdgeCasesFacadeImpl extends EdgeCases.EdgeCasesFacade {

	private static final int MAX_NUMBER_OF_EDGE_CASES = 1000;

	private static final Logger LOG = Logger.getLogger(EdgeCasesFacadeImpl.class.getName());

	private final Store<Boolean> warningAlreadyLogged =
		Store.create(
			Tuple.of(EdgeCasesFacadeImpl.class, "warning"),
			Lifespan.PROPERTY,
			() -> false
		);

	@Override
	public <T> EdgeCases<T> fromSuppliers(final List<Supplier<Shrinkable<T>>> suppliers) {
		return EdgeCasesSupport.fromSuppliers(suppliers);
	}

	@Override
	public <T> EdgeCases<T> concat(List<EdgeCases<T>> edgeCases) {
		return EdgeCasesSupport.concat(edgeCases);
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

	private void logTooManyEdgeCases(int maxNumberOfEdgeCases) {
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

	@Override
	public <T> EdgeCases<T> filter(EdgeCases<T> self, Predicate<T> filterPredicate) {
		List<Supplier<Shrinkable<T>>> filteredSuppliers =
			self.suppliers().stream()
				.filter(supplier -> filterPredicate.test(supplier.get().value()))
				.map(supplier -> (Supplier<Shrinkable<T>>) () -> new FilteredShrinkable<>(supplier.get(), filterPredicate))
				.collect(Collectors.toList());
		return EdgeCases.fromSuppliers(filteredSuppliers);
	}

	@Override
	public <T> EdgeCases<T> ignoreException(final EdgeCases<T> self, final Class<? extends Throwable> exceptionType) {
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
}
