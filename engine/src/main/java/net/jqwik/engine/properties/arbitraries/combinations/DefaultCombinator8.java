package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public class DefaultCombinator8<T1, T2, T3, T4, T5, T6, T7, T8>
	extends DefaultCombinator7<T1, T2, T3, T4, T5, T6, T7>
	implements Combinators.Combinator8<T1, T2, T3, T4, T5, T6, T7, T8> {

	protected final Arbitrary<T8> a8;

	public DefaultCombinator8(
		Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4,
		Arbitrary<T5> a5, Arbitrary<T6> a6, Arbitrary<T7> a7, Arbitrary<T8> a8
	) {
		super(a1, a2, a3, a4, a5, a6, a7);
		this.a8 = a8;
	}

	@Override
	public <R> Arbitrary<R> as(Combinators.F8<T1, T2, T3, T4, T5, T6, T7, T8, R> combinator) {
		return new CombineArbitrary<>(combineFunction(combinator), a1, a2, a3, a4, a5, a6, a7, a8);
	}

	@Override
	public Combinators.Combinator8<T1, T2, T3, T4, T5, T6, T7, T8> filter(Combinators.F8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean> filter) {
		return new Filtered<>(a1, a2, a3, a4, a5, a6, a7, a8, filter);
	}

	@SuppressWarnings("unchecked")
	protected <R> Function<List<Object>, R> combineFunction(Combinators.F8<T1, T2, T3, T4, T5, T6, T7, T8, R> combinator) {
		return params -> combinator.apply(
			(T1) params.get(0), (T2) params.get(1),
			(T3) params.get(2), (T4) params.get(3),
			(T5) params.get(4), (T6) params.get(5),
			(T7) params.get(6), (T8) params.get(7)
		);
	}

	private static class Filtered<T1, T2, T3, T4, T5, T6, T7, T8> extends DefaultCombinator8<T1, T2, T3, T4, T5, T6, T7, T8> {
		private final Combinators.F8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean> filter;

		private Filtered(
			Arbitrary<T1> a1,
			Arbitrary<T2> a2,
			Arbitrary<T3> a3,
			Arbitrary<T4> a4,
			Arbitrary<T5> a5,
			Arbitrary<T6> a6,
			Arbitrary<T7> a7,
			Arbitrary<T8> a8,
			Combinators.F8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean> filter
		) {
			super(a1, a2, a3, a4, a5, a6, a7, a8);
			this.filter = filter;
		}

		@Override
		public <R> Arbitrary<R> as(Combinators.F8<T1, T2, T3, T4, T5, T6, T7, T8, R> combinator) {
			return new CombineArbitrary<>(Function.identity(), a1, a2, a3, a4, a5, a6, a7, a8)
					   .filter(combineFunction(filter)::apply)
					   .map(combineFunction(combinator));
		}

		@Override
		public Combinators.Combinator8<T1, T2, T3, T4, T5, T6, T7, T8> filter(Combinators.F8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean> filter) {
			return super.filter(combineFilters(this.filter, filter));
		}

		private Combinators.F8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean> combineFilters(
			Combinators.F8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean> first,
			Combinators.F8<T1, T2, T3, T4, T5, T6, T7, T8, Boolean> second
		) {
			return (p1, p2, p3, p4, p5, p6, p7, p8) -> first.apply(p1, p2, p3, p4, p5, p6, p7, p8) && second.apply(p1, p2, p3, p4, p5, p6, p7, p8);
		}

	}
}

