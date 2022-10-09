package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

class FilteredCombinator3<T1, T2, T3> implements Combinators.Combinator3<T1, T2, T3> {
	private final Combinators.F3<T1, T2, T3, Boolean> filter;
	private final Arbitrary<T1> a1;
	private final Arbitrary<T2> a2;
	private final Arbitrary<T3> a3;

	FilteredCombinator3(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Combinators.F3<T1, T2, T3, Boolean> filter) {
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
		this.filter = filter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> Arbitrary<R> as(Combinators.F3<T1, T2, T3, @NotNull R> combinator) {
		return new CombineArbitrary<>(Function.identity(), a1, a2, a3)
				   .filter(params -> filter.apply((T1) params.get(0), (T2) params.get(1), (T3) params.get(2)))
				   .map(combineFunction(combinator));
	}

	@Override
	public Combinators.Combinator3<T1, T2, T3> filter(Combinators.F3<T1, T2, T3, Boolean> filter) {
		return new FilteredCombinator3<>(a1, a2, a3, combineFilters(this.filter, filter));
	}

	@SuppressWarnings("unchecked")
	private <R> Function<List<Object>, R> combineFunction(Combinators.F3<T1, T2, T3, R> combinator) {
		return params -> combinator
							 .apply(
								 (T1) params.get(0), (T2) params.get(1),
								 (T3) params.get(2)
							 );
	}

	private Combinators.F3<T1, T2, T3, Boolean> combineFilters(Combinators.F3<T1, T2, T3, Boolean> filter1, Combinators.F3<T1, T2, T3, Boolean> filter2) {
		return (t1, t2, t3) -> filter1.apply(t1, t2, t3) && filter2.apply(t1, t2, t3);
	}

}
