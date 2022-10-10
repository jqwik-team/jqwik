package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.*;

import static net.jqwik.api.Combinators.*;

public class FilteredCombinator6<T1, T2, T3, T4, T5, T6> implements Combinator6<T1, T2, T3, T4, T5, T6> {
	private final Arbitrary<T1> a1;
	private final Arbitrary<T2> a2;
	private final Arbitrary<T3> a3;
	private final Arbitrary<T4> a4;
	private final Arbitrary<T5> a5;
	private final Arbitrary<T6> a6;
	private final F6<T1, T2, T3, T4, T5, T6, Boolean> filter;

	public FilteredCombinator6(
		Arbitrary<T1> a1,
		Arbitrary<T2> a2,
		Arbitrary<T3> a3,
		Arbitrary<T4> a4,
		Arbitrary<T5> a5,
		Arbitrary<T6> a6,
		F6<T1, T2, T3, T4, T5, T6, Boolean> filter
	) {
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
		this.a4 = a4;
		this.a5 = a5;
		this.a6 = a6;
		this.filter = filter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> Arbitrary<R> as(F6<T1, T2, T3, T4, T5, T6, R> combinator) {
		return new CombineArbitrary<>(Function.identity(), a1, a2, a3, a4, a5, a6)
				   .filter(params -> filter.apply(
					   (T1) params.get(0), (T2) params.get(1),
					   (T3) params.get(2), (T4) params.get(3),
					   (T5) params.get(4), (T6) params.get(5)
				   ))
				   .map(combineFunction(combinator));
	}

	@Override
	public Combinator6<T1, T2, T3, T4, T5, T6> filter(F6<T1, T2, T3, T4, T5, T6, Boolean> filter) {
		return new FilteredCombinator6<>(a1, a2, a3, a4, a5, a6, combineFilters(this.filter, filter));
	}

	@NotNull
	private F6<T1, T2, T3, T4, T5, T6, Boolean> combineFilters(F6<T1, T2, T3, T4, T5, T6, Boolean> first, F6<T1, T2, T3, T4, T5, T6, Boolean> second) {
		return (p1, p2, p3, p4, p5, p6) -> first.apply(p1, p2, p3, p4, p5, p6) && second.apply(p1, p2, p3, p4, p5, p6);
	}

	@SuppressWarnings("unchecked")
	private <R> Function<List<Object>, R> combineFunction(F6<T1, T2, T3, T4, T5, T6, R> combinator) {
		return params -> combinator
							 .apply(
								 (T1) params.get(0), (T2) params.get(1),
								 (T3) params.get(2), (T4) params.get(3),
								 (T5) params.get(4), (T6) params.get(5)
							 );
	}

}