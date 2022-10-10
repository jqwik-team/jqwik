package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.*;

public class FilteredListCombinator<T> implements Combinators.ListCombinator<T> {
	private final Arbitrary<T>[] arbitraries;
	private final Predicate<List<T>> filter;

	public FilteredListCombinator(Arbitrary<T>[] arbitraries, Predicate<List<T>> filter) {
		this.arbitraries = arbitraries;
		this.filter = filter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> Arbitrary<R> as(Function<List<T>, @NotNull R> combinator) {
		return new CombineArbitrary<>(Function.identity(), arbitraries)
				   .filter(params -> filter.test((List<T>) params))
				   .map(combineFunction(combinator));
	}

	@SuppressWarnings("unchecked")
	private <R> Function<List<Object>, R> combineFunction(Function<List<T>, R> combinator) {
		return params -> combinator.apply((List<T>) params);
	}

	@Override
	public Combinators.ListCombinator<T> filter(Predicate<List<T>> filter) {
		return new FilteredListCombinator<>(arbitraries, combineFilters(this.filter, filter));
	}

	private Predicate<List<T>> combineFilters(Predicate<List<T>> first, Predicate<List<T>> second) {
		return first.and(second);
	}
}
