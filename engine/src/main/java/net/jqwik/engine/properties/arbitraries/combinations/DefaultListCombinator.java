package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

public class DefaultListCombinator<T extends @Nullable Object> implements Combinators.ListCombinator<T> {

	protected final Arbitrary<T>[] arbitraries;

	@SuppressWarnings("unchecked")
	public DefaultListCombinator(List<? extends Arbitrary<T>> listOfArbitraries) {
		this(listOfArbitraries.toArray(new Arbitrary[0]));
	}

	private DefaultListCombinator(Arbitrary<T>[] arbitraries) {
		this.arbitraries = arbitraries;
	}

	@Override
	public <R extends @Nullable Object> Arbitrary<R> as(Function<? super List<T>, ? extends R> combinator) {
		return new CombineArbitrary<>(combineFunction(combinator), arbitraries);
	}

	@API(status = EXPERIMENTAL, since = "1.7.1")
	public Combinators.ListCombinator<T> filter(Predicate<? super List<? extends T>> filter) {
		return new Filtered<>(arbitraries, filter);
	}

	@SuppressWarnings("unchecked")
	protected <R extends @Nullable Object> Function<List<?>, R> combineFunction(Function<? super List<T>, ? extends R> combinator) {
		return params -> combinator.apply((List<T>) params);
	}

	private static class Filtered<T extends @Nullable Object> extends DefaultListCombinator<T> {
		private final Predicate<? super List<? extends T>> filter;

		private Filtered(Arbitrary<T>[] arbitraries, Predicate<? super List<? extends T>> filter) {
			super(arbitraries);
			this.filter = filter;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <R extends @Nullable Object> Arbitrary<R> as(Function<? super List<T>, ? extends R> combinator) {
			Predicate<? super List<?>> filterPredicate = params -> filter.test((List<T>) params);
			return new CombineArbitrary<>(Function.identity(), arbitraries)
					   .filter(filterPredicate)
					   .map(combineFunction(combinator));
		}

		@Override
		public Combinators.ListCombinator<T> filter(Predicate<? super List<? extends T>> filter) {
			return super.filter(combineFilters(this.filter, filter));
		}

		private Predicate<? super List<? extends T>> combineFilters(Predicate<? super List<? extends T>> first, Predicate<? super List<? extends T>> second) {
			return it -> first.test(it) && second.test(it);
		}
	}
}
