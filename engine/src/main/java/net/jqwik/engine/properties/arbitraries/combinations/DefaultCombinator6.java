package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

public class DefaultCombinator6<T1 extends @Nullable Object, T2 extends @Nullable Object, T3 extends @Nullable Object, T4 extends @Nullable Object, T5 extends @Nullable Object, T6 extends @Nullable Object>
	extends DefaultCombinator5<T1, T2, T3, T4, T5>
	implements Combinators.Combinator6<T1, T2, T3, T4, T5, T6> {

	protected final Arbitrary<T6> a6;

	public DefaultCombinator6(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4, Arbitrary<T5> a5, Arbitrary<T6> a6) {
		super(a1, a2, a3, a4, a5);
		this.a6 = a6;
	}

	@Override
	public <R extends @Nullable Object> Arbitrary<R> as(Combinators.F6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R> combinator) {
		return new CombineArbitrary<>(combineFunction(combinator), a1, a2, a3, a4, a5, a6);
	}

	@Override
	public Combinators.Combinator6<T1, T2, T3, T4, T5, T6> filter(Combinators.F6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, Boolean> filter) {
		return new Filtered<>(a1, a2, a3, a4, a5, a6, filter);
	}

	@SuppressWarnings("unchecked")
	protected <R extends @Nullable Object> Function<List<?>, R> combineFunction(Combinators.F6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R> combinator) {
		return params -> combinator.apply(
			(T1) params.get(0), (T2) params.get(1),
			(T3) params.get(2), (T4) params.get(3),
			(T5) params.get(4), (T6) params.get(5)
		);
	}

	private static class Filtered<T1, T2, T3, T4, T5, T6> extends DefaultCombinator6<T1, T2, T3, T4, T5, T6> {
		private final Combinators.F6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, Boolean> filter;

		private Filtered(
			Arbitrary<T1> a1,
			Arbitrary<T2> a2,
			Arbitrary<T3> a3,
			Arbitrary<T4> a4,
			Arbitrary<T5> a5,
			Arbitrary<T6> a6,
			Combinators.F6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, Boolean> filter
		) {
			super(a1, a2, a3, a4, a5, a6);
			this.filter = filter;
		}

		@Override
		public <R extends @Nullable Object> Arbitrary<R> as(Combinators.F6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? extends R> combinator) {
			return new CombineArbitrary<>(Function.identity(), a1, a2, a3, a4, a5, a6)
					   .filter(combineFunction(filter)::apply)
					   .map(combineFunction(combinator));
		}

		@Override
		public Combinators.Combinator6<T1, T2, T3, T4, T5, T6> filter(Combinators.F6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, Boolean> filter) {
			return super.filter(combineFilters(this.filter, filter));
		}

		private Combinators.F6<T1, T2, T3, T4, T5, T6, Boolean> combineFilters(
			Combinators.F6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, Boolean> first,
			Combinators.F6<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, Boolean> second
		) {
			return (p1, p2, p3, p4, p5, p6) -> first.apply(p1, p2, p3, p4, p5, p6)
												   && second.apply(p1, p2, p3, p4, p5, p6);
		}

	}
}

