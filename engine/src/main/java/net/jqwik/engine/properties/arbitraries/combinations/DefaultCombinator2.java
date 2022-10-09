package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.*;

public class DefaultCombinator2<T1, T2> implements Combinators.Combinator2<T1, T2> {
	private final Arbitrary<T1> a1;
	private final Arbitrary<T2> a2;

	public DefaultCombinator2(Arbitrary<T1> a1, Arbitrary<T2> a2) {
		this.a1 = a1;
		this.a2 = a2;
	}

	@Override
	public <R> Arbitrary<R> as(Combinators.F2<T1, T2, @NotNull R> combinator) {
		return new CombineArbitrary<>(combineFunction(combinator), a1, a2);
	}

	@Override
	public Combinators.Combinator2<T1, T2> filter(Combinators.F2<T1, T2, Boolean> filter) {
		return new FilteredCombinator2<>(a1, a2, filter);
	}

	@SuppressWarnings("unchecked")
	private <R> Function<List<Object>, R> combineFunction(Combinators.F2<T1, T2, R> combinator2) {
		return params -> combinator2.apply((T1) params.get(0), (T2) params.get(1));
	}

}
