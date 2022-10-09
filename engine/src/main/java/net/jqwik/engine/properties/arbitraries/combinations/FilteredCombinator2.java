package net.jqwik.engine.properties.arbitraries.combinations;

import java.util.*;
import java.util.function.*;

import org.jetbrains.annotations.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

class FilteredCombinator2<T1, T2> implements Combinators.Combinator2<T1, T2> {
	private final Combinators.F2<T1, T2, Boolean> filter;
	private final Arbitrary<T1> a1;
	private final Arbitrary<T2> a2;

	FilteredCombinator2(Arbitrary<T1> a1, Arbitrary<T2> a2, Combinators.F2<T1, T2, Boolean> filter) {
		this.a1 = a1;
		this.a2 = a2;
		this.filter = filter;
	}

	@Override
	public <R> Arbitrary<R> as(Combinators.F2<T1, T2, @NotNull R> combinator) {
		Arbitrary<Tuple2<T1, T2>> unfilteredArbitrary = new CombineArbitrary<>(combineToTuple(), a1, a2);
		return unfilteredArbitrary
				   .filter(tuple -> filter.apply(tuple.get1(), tuple.get2()))
				   .map(tuple -> combinator.apply(tuple.get1(), tuple.get2()));
	}

	@Override
	public Combinators.Combinator2<T1, T2> filter(Combinators.F2<T1, T2, Boolean> filter) {
		return new FilteredCombinator2<>(a1, a2, combineFilters(this.filter, filter));
	}

	@SuppressWarnings("unchecked")
	private Function<List<Object>, Tuple2<T1, T2>> combineToTuple() {
		return params -> Tuple.of((T1) params.get(0), (T2) params.get(1));
	}

	private Combinators.F2<T1,T2, Boolean> combineFilters(Combinators.F2<T1,T2, Boolean> first, Combinators.F2<T1,T2, Boolean> second) {
		return (t1, t2) -> first.apply(t1, t2) && second.apply(t1, t2);
	}

}
