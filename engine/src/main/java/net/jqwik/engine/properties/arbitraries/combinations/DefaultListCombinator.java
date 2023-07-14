package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

public class DefaultListCombinator<T> implements Combinators.ListCombinator<T> {

	protected final Arbitrary<T>[] arbitraries;

	@SuppressWarnings("unchecked")
	public DefaultListCombinator(List<Arbitrary<T>> listOfArbitraries) {
		this(listOfArbitraries.toArray(new Arbitrary[0]));
	}

	private DefaultListCombinator(Arbitrary<T>[] arbitraries) {
		this.arbitraries = arbitraries;
	}

	@Override
	public <R> Arbitrary<R> as(Function<List<T>, @NonNull R> combinator) {
		return new CombineArbitrary<>(combineFunction(combinator), arbitraries);
	}

	@API(status = EXPERIMENTAL, since = "1.7.1")
	public Combinators.ListCombinator<T> filter(Predicate<List<T>> filter) {
		return new Filtered<>(arbitraries, filter);
	}

	@SuppressWarnings("unchecked")
	protected <R> Function<List<Object>, R> combineFunction(Function<List<T>, R> combinator) {
		return params -> combinator.apply((List<T>) params);
	}

	private static class Filtered<T> extends DefaultListCombinator<T> {
		private final Predicate<List<T>> filter;

		private Filtered(Arbitrary<T>[] arbitraries, Predicate<List<T>> filter) {
			super(arbitraries);
			this.filter = filter;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <R> Arbitrary<R> as(Function<List<T>, @NonNull R> combinator) {
			Predicate<List<Object>> filterPredicate = params -> filter.test((List<T>) params);
			return new CombineArbitrary<>(Function.identity(), arbitraries)
					   .filter(filterPredicate)
					   .map(combineFunction(combinator));
		}

		@Override
		public Combinators.ListCombinator<T> filter(Predicate<List<T>> filter) {
			return super.filter(combineFilters(this.filter, filter));
		}

		private Predicate<List<T>> combineFilters(Predicate<List<T>> first, Predicate<List<T>> second) {
			return first.and(second);
		}
	}
}
