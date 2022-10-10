package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public class DefaultCombinator4<T1, T2, T3, T4> implements Combinators.Combinator4<T1, T2, T3, T4> {
	private final Arbitrary<T1> a1;
	private final Arbitrary<T2> a2;
	private final Arbitrary<T3> a3;
	private final Arbitrary<T4> a4;

	public DefaultCombinator4(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4) {
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
		this.a4 = a4;
	}

	@Override
	public <R> Arbitrary<R> as(Combinators.F4<T1, T2, T3, T4, R> combinator) {
		return new CombineArbitrary<>(combineFunction(combinator), a1, a2, a3, a4);
	}

	@Override
	public Combinators.Combinator4<T1, T2, T3, T4> filter(Combinators.F4<T1, T2, T3, T4, Boolean> filter) {
		return new FilteredCombinator4<>(a1, a2, a3, a4, filter);
	}

	@SuppressWarnings("unchecked")
	private <R> Function<List<Object>, R> combineFunction(Combinators.F4<T1, T2, T3, T4, R> combinator) {
		return params -> combinator.apply((T1) params.get(0), (T2) params.get(1), (T3) params.get(2), (T4) params.get(3));
	}
}

