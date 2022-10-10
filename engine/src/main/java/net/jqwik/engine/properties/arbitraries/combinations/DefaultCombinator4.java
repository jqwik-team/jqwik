package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.*;

public class DefaultCombinator4<T1, T2, T3, T4>
	extends DefaultCombinator3<T1, T2, T3>
	implements Combinators.Combinator4<T1, T2, T3, T4> {

	protected final Arbitrary<T4> a4;

	public DefaultCombinator4(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4) {
		super(a1, a2, a3);
		this.a4 = a4;
	}

	@Override
	public <R> Arbitrary<R> as(Combinators.F4<T1, T2, T3, T4, R> combinator) {
		return new CombineArbitrary<>(combineFunction(combinator), a1, a2, a3, a4);
	}

	@Override
	public Combinators.Combinator4<T1, T2, T3, T4> filter(Combinators.F4<T1, T2, T3, T4, Boolean> filter) {
		return new Filtered<>(a1, a2, a3, a4, filter);
	}

	@SuppressWarnings("unchecked")
	protected <R> Function<List<Object>, R> combineFunction(Combinators.F4<T1, T2, T3, T4, R> combinator) {
		return params -> combinator.apply((T1) params.get(0), (T2) params.get(1), (T3) params.get(2), (T4) params.get(3));
	}

	private static class Filtered<T1, T2, T3, T4> extends DefaultCombinator4<T1, T2, T3, T4> {
		private final Combinators.F4<T1, T2, T3, T4, Boolean> filter;

		private Filtered(
			Arbitrary<T1> a1,
			Arbitrary<T2> a2,
			Arbitrary<T3> a3,
			Arbitrary<T4> a4,
			Combinators.F4<T1, T2, T3, T4, Boolean> filter
		) {
			super(a1, a2, a3, a4);
			this.filter = filter;
		}

		@Override
		public <R> Arbitrary<R> as(Combinators.F4<T1, T2, T3, T4, R> combinator) {
			return new CombineArbitrary<>(Function.identity(), a1, a2, a3, a4)
					   .filter(combineFunction(filter)::apply)
					   .map(combineFunction(combinator));
		}

		@Override
		public Combinators.Combinator4<T1, T2, T3, T4> filter(Combinators.F4<T1, T2, T3, T4, Boolean> filter) {
			return super.filter(combineFilters(this.filter, filter));
		}

		@NotNull
		private Combinators.F4<T1, T2, T3, T4, Boolean> combineFilters(
			Combinators.F4<T1, T2, T3, T4, Boolean> first,
			Combinators.F4<T1, T2, T3, T4, Boolean> second
		) {
			return (p1, p2, p3, p4) -> first.apply(p1, p2, p3, p4) && second.apply(p1, p2, p3, p4);
		}

	}
}

