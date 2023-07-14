package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public class DefaultCombinator7<T1, T2, T3, T4, T5, T6, T7>
	extends DefaultCombinator6<T1, T2, T3, T4, T5, T6>
	implements Combinators.Combinator7<T1, T2, T3, T4, T5, T6, T7> {

	protected final Arbitrary<T7> a7;

	public DefaultCombinator7(
		Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4, Arbitrary<T5> a5, Arbitrary<T6> a6, Arbitrary<T7> a7
	) {
		super(a1, a2, a3, a4, a5, a6);
		this.a7 = a7;
	}

	@Override
	public <R> Arbitrary<R> as(Combinators.F7<T1, T2, T3, T4, T5, T6, T7, R> combinator) {
		return new CombineArbitrary<>(combineFunction(combinator), a1, a2, a3, a4, a5, a6, a7);
	}

	@Override
	public Combinators.Combinator7<T1, T2, T3, T4, T5, T6, T7> filter(Combinators.F7<T1, T2, T3, T4, T5, T6, T7, Boolean> filter) {
		return new Filtered<>(a1, a2, a3, a4, a5, a6, a7, filter);
	}

	@SuppressWarnings("unchecked")
	protected <R> Function<List<Object>, R> combineFunction(Combinators.F7<T1, T2, T3, T4, T5, T6, T7, R> combinator) {
		return params -> combinator.apply(
			(T1) params.get(0), (T2) params.get(1),
			(T3) params.get(2), (T4) params.get(3),
			(T5) params.get(4), (T6) params.get(5),
			(T7) params.get(6)
		);
	}

	private static class Filtered<T1, T2, T3, T4, T5, T6, T7> extends DefaultCombinator7<T1, T2, T3, T4, T5, T6, T7> {
		private final Combinators.F7<T1, T2, T3, T4, T5, T6, T7, Boolean> filter;

		private Filtered(
			Arbitrary<T1> a1,
			Arbitrary<T2> a2,
			Arbitrary<T3> a3,
			Arbitrary<T4> a4,
			Arbitrary<T5> a5,
			Arbitrary<T6> a6,
			Arbitrary<T7> a7,
			Combinators.F7<T1, T2, T3, T4, T5, T6, T7, Boolean> filter
		) {
			super(a1, a2, a3, a4, a5, a6, a7);
			this.filter = filter;
		}

		@Override
		public <R> Arbitrary<R> as(Combinators.F7<T1, T2, T3, T4, T5, T6, T7, R> combinator) {
			return new CombineArbitrary<>(Function.identity(), a1, a2, a3, a4, a5, a6, a7)
					   .filter(combineFunction(filter)::apply)
					   .map(combineFunction(combinator));
		}

		@Override
		public Combinators.Combinator7<T1, T2, T3, T4, T5, T6, T7> filter(Combinators.F7<T1, T2, T3, T4, T5, T6, T7, Boolean> filter) {
			return super.filter(combineFilters(this.filter, filter));
		}

		private Combinators.F7<T1, T2, T3, T4, T5, T6, T7, Boolean> combineFilters(
			Combinators.F7<T1, T2, T3, T4, T5, T6, T7, Boolean> first,
			Combinators.F7<T1, T2, T3, T4, T5, T6, T7, Boolean> second
		) {
			return (p1, p2, p3, p4, p5, p6, p7) -> first.apply(p1, p2, p3, p4, p5, p6, p7) && second.apply(p1, p2, p3, p4, p5, p6, p7);
		}

	}
}

