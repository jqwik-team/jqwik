package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.*;

public class DefaultCombinator5<T1, T2, T3, T4, T5>
	extends DefaultCombinator4<T1, T2, T3, T4>
	implements Combinators.Combinator5<T1, T2, T3, T4, T5> {

	protected final Arbitrary<T5> a5;

	public DefaultCombinator5(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4, Arbitrary<T5> a5) {
		super(a1, a2, a3, a4);
		this.a5 = a5;
	}

	@Override
	public <R> Arbitrary<R> as(Combinators.F5<T1, T2, T3, T4, T5, R> combinator) {
		return new CombineArbitrary<>(combineFunction(combinator), a1, a2, a3, a4, a5);
	}

	@Override
	public Combinators.Combinator5<T1, T2, T3, T4, T5> filter(Combinators.F5<T1, T2, T3, T4, T5, Boolean> filter) {
		return new Filtered<>(a1, a2, a3, a4, a5, filter);
	}

	@SuppressWarnings("unchecked")
	protected <R> Function<List<Object>, R> combineFunction(Combinators.F5<T1, T2, T3, T4, T5, R> combinator) {
		return params -> combinator.apply(
			(T1) params.get(0), (T2) params.get(1),
			(T3) params.get(2), (T4) params.get(3),
			(T5) params.get(4)
		);
	}

	private static class Filtered<T1, T2, T3, T4, T5> extends DefaultCombinator5<T1, T2, T3, T4, T5> {
		private final Combinators.F5<T1, T2, T3, T4, T5, Boolean> filter;

		private Filtered(
			Arbitrary<T1> a1,
			Arbitrary<T2> a2,
			Arbitrary<T3> a3,
			Arbitrary<T4> a4,
			Arbitrary<T5> a5,
			Combinators.F5<T1, T2, T3, T4, T5, Boolean> filter
		) {
			super(a1, a2, a3, a4, a5);
			this.filter = filter;
		}

		@Override
		public <R> Arbitrary<R> as(Combinators.F5<T1, T2, T3, T4, T5, R> combinator) {
			return new CombineArbitrary<>(Function.identity(), a1, a2, a3, a4, a5)
					   .filter(combineFunction(filter)::apply)
					   .map(combineFunction(combinator));
		}

		@Override
		public Combinators.Combinator5<T1, T2, T3, T4, T5> filter(Combinators.F5<T1, T2, T3, T4, T5, Boolean> filter) {
			return super.filter(combineFilters(this.filter, filter));
		}

		@NotNull
		private Combinators.F5<T1, T2, T3, T4, T5, Boolean> combineFilters(
			Combinators.F5<T1, T2, T3, T4, T5, Boolean> first,
			Combinators.F5<T1, T2, T3, T4, T5, Boolean> second
		) {
			return (p1, p2, p3, p4, p5) -> first.apply(p1, p2, p3, p4, p5) && second.apply(p1, p2, p3, p4, p5);
		}

	}
}

