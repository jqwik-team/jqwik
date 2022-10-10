package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.*;

import static net.jqwik.api.Combinators.*;

public class FilteredCombinator5<T1, T2, T3, T4, T5> implements Combinator5<T1, T2, T3, T4, T5> {
	private final Arbitrary<T1> a1;
	private final Arbitrary<T2> a2;
	private final Arbitrary<T3> a3;
	private final Arbitrary<T4> a4;
	private final Arbitrary<T5> a5;
	private final F5<T1, T2, T3, T4, T5, Boolean> filter;

	public FilteredCombinator5(
		Arbitrary<T1> a1,
		Arbitrary<T2> a2,
		Arbitrary<T3> a3,
		Arbitrary<T4> a4,
		Arbitrary<T5> a5,
		F5<T1, T2, T3, T4, T5, Boolean> filter
	) {
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
		this.a4 = a4;
		this.a5 = a5;
		this.filter = filter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> Arbitrary<R> as(F5<T1, T2, T3, T4, T5, R> combinator) {
		return new CombineArbitrary<>(Function.identity(), a1, a2, a3, a4, a5)
				   .filter(params -> filter.apply(
					   (T1) params.get(0), (T2) params.get(1),
					   (T3) params.get(2), (T4) params.get(3),
					   (T5) params.get(4)
				   ))
				   .map(combineFunction(combinator));
	}

	@Override
	public Combinator5<T1, T2, T3, T4, T5> filter(F5<T1, T2, T3, T4, T5, Boolean> filter) {
		return new FilteredCombinator5<>(a1, a2, a3, a4, a5, combineFilters(this.filter, filter));
	}

	@NotNull
	private F5<T1, T2, T3, T4, T5, Boolean> combineFilters(F5<T1, T2, T3, T4, T5, Boolean> first, F5<T1, T2, T3, T4, T5, Boolean> second) {
		return (p1, p2, p3, p4, p5) -> first.apply(p1, p2, p3, p4, p5) && second.apply(p1, p2, p3, p4, p5);
	}

	@SuppressWarnings("unchecked")
	private <R> Function<List<Object>, R> combineFunction(F5<T1, T2, T3, T4, T5, R> combinator) {
		return params -> combinator
							 .apply(
								 (T1) params.get(0), (T2) params.get(1),
								 (T3) params.get(2), (T4) params.get(3),
								 (T5) params.get(4)
							 );
	}

}