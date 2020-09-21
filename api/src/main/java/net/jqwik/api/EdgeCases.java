package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.3.0")
public interface EdgeCases<T> extends Iterable<Shrinkable<T>> {

	@API(status = INTERNAL)
	abstract class EdgeCasesFacade {
		private static final EdgeCases.EdgeCasesFacade implementation;

		static {
			implementation = FacadeLoader.load(EdgeCases.EdgeCasesFacade.class);
		}

		public abstract <T> EdgeCases<T> fromSuppliers(List<Supplier<Shrinkable<T>>> suppliers);

		public abstract <T> EdgeCases<T> concat(List<EdgeCases<T>> edgeCases);

		public abstract <T, U> EdgeCases<U> mapShrinkable(EdgeCases<T> self, Function<Shrinkable<T>, Shrinkable<U>> mapper);

		public abstract <T, U> EdgeCases<U> flatMapArbitrary(EdgeCases<T> self, Function<T, Arbitrary<U>> mapper);

		public abstract <T> EdgeCases<T> filter(EdgeCases<T> self, Predicate<T> filterPredicate);

		public abstract <T> EdgeCases<T> ignoreException(EdgeCases<T> self, Class<? extends Throwable> exceptionType);
	}

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
	static <T> EdgeCases<T> fromSuppliers(List<Supplier<Shrinkable<T>>> suppliers) {
		return EdgeCasesFacade.implementation.fromSuppliers(suppliers);
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
	// TODO: Remove as soon as Arbitrary.injectNull() uses frequencyOf()
	static <T> EdgeCases<T> concat(EdgeCases<T>... rest) {
		return EdgeCasesFacade.implementation.concat(Arrays.asList(rest));
	}

	@API(status = INTERNAL)
	default <U> EdgeCases<U> map(Function<T, U> mapper) {
		return mapShrinkable(tShrinkable -> tShrinkable.map(mapper));
	}

	@API(status = INTERNAL)
	default <U> EdgeCases<U> mapShrinkable(Function<Shrinkable<T>, Shrinkable<U>> mapper) {
		return EdgeCasesFacade.implementation.mapShrinkable(this, mapper);
	}

	@API(status = INTERNAL)
	default EdgeCases<T> filter(Predicate<T> filterPredicate) {
		return EdgeCasesFacade.implementation.filter(this, filterPredicate);
	}

	@API(status = INTERNAL)
	default <U> EdgeCases<U> flatMapArbitrary(Function<T, Arbitrary<U>> mapper) {
		return EdgeCasesFacade.implementation.flatMapArbitrary(this, mapper);
	}

	@API(status = INTERNAL)
	default EdgeCases<T> ignoreException(Class<? extends Throwable> exceptionType) {
		return EdgeCasesFacade.implementation.ignoreException(this, exceptionType);
	}

	@API(status = INTERNAL)
	default EdgeCases<T> dontShrink() {
		return () -> EdgeCases.this.suppliers()
								   .stream()
								   .map(supplier -> (Supplier<Shrinkable<T>>) () -> supplier.get().makeUnshrinkable())
								   .collect(Collectors.toList());
	}


}
