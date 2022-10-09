package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.*;

public class DefaultCombinator3<T1, T2, T3> implements Combinators.Combinator3<T1, T2, T3> {
	private final Arbitrary<T1> a1;
	private final Arbitrary<T2> a2;
	private final Arbitrary<T3> a3;

	public DefaultCombinator3(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3) {
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
	}

	@Override
	public <R> Arbitrary<R> as(Combinators.F3<T1, T2, T3, @NotNull R> combinator) {
		return new CombineArbitrary<>(combineFunction(combinator), a1, a2, a3);
	}

	@Override
	public Combinators.Combinator3<T1, T2, T3> filter(Combinators.F3<T1, T2, T3, Boolean> filter) {
		return new FilteredCombinator3<>(a1, a2, a3, filter);
	}

	@SuppressWarnings("unchecked")
	private <R> Function<List<Object>, R> combineFunction(Combinators.F3<T1, T2, T3, R> combinator) {
		return params -> combinator.apply((T1) params.get(0), (T2) params.get(1), (T3) params.get(2));
	}

}
