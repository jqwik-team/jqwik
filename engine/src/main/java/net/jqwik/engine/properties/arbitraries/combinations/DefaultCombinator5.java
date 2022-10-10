package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public class DefaultCombinator5<T1, T2, T3, T4, T5> implements Combinators.Combinator5<T1, T2, T3, T4, T5> {
	private final Arbitrary<T1> a1;
	private final Arbitrary<T2> a2;
	private final Arbitrary<T3> a3;
	private final Arbitrary<T4> a4;
	private final Arbitrary<T5> a5;

	public DefaultCombinator5(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4, Arbitrary<T5> a5) {
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
		this.a4 = a4;
		this.a5 = a5;
	}

	@Override
	public <R> Arbitrary<R> as(Combinators.F5<T1, T2, T3, T4, T5, R> combinator) {
		return new CombineArbitrary<>(combineFunction(combinator), a1, a2, a3, a4, a5);
	}

	@Override
	public Combinators.Combinator5<T1, T2, T3, T4, T5> filter(Combinators.F5<T1, T2, T3, T4, T5, Boolean> filter) {
		return new FilteredCombinator5<>(a1, a2, a3, a4, a5, filter);
	}

	@SuppressWarnings("unchecked")
	private <R> Function<List<Object>, R> combineFunction(Combinators.F5<T1, T2, T3, T4, T5, R> combinator) {
		return params -> combinator.apply(
			(T1) params.get(0), (T2) params.get(1),
			(T3) params.get(2), (T4) params.get(3),
			(T5) params.get(4)
		);
	}
}

