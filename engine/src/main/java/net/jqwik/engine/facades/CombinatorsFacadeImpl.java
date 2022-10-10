package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.combinations.*;

/**
 * Is loaded through reflection in api module
 */
public class CombinatorsFacadeImpl extends Combinators.CombinatorsFacade {

	@Override
	public <T1, T2> Combinators.Combinator2<T1, T2> combine2(Arbitrary<T1> a1, Arbitrary<T2> a2) {
		return new DefaultCombinator2<>(a1, a2);
	}

	@Override
	public <T1, T2, T3> Combinators.Combinator3<T1, T2, T3> combine3(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3) {
		return new DefaultCombinator3<>(a1, a2, a3);
	}

	@Override
	public <T1, T2, T3, T4> Combinators.Combinator4<T1, T2, T3, T4> combine4(
		Arbitrary<T1> a1,
		Arbitrary<T2> a2,
		Arbitrary<T3> a3,
		Arbitrary<T4> a4
	) {
		return new DefaultCombinator4<>(a1, a2, a3, a4);
	}

	@Override
	public <T1, T2, T3, T4, T5> Combinators.Combinator5<T1, T2, T3, T4, T5> combine5(
		Arbitrary<T1> a1,
		Arbitrary<T2> a2,
		Arbitrary<T3> a3,
		Arbitrary<T4> a4,
		Arbitrary<T5> a5
	) {
		return new DefaultCombinator5<>(a1, a2, a3, a4, a5);
	}

	@Override
	public <T1, T2, T3, T4, T5, T6> Combinators.Combinator6<T1, T2, T3, T4, T5, T6> combine6(
		Arbitrary<T1> a1,
		Arbitrary<T2> a2,
		Arbitrary<T3> a3,
		Arbitrary<T4> a4,
		Arbitrary<T5> a5,
		Arbitrary<T6> a6
	) {
		return new DefaultCombinator6<>(a1, a2, a3, a4, a5, a6);
	}

	@Override
	public <T1, T2, T3, T4, T5, T6, T7> Combinators.Combinator7<T1, T2, T3, T4, T5, T6, T7> combine7(
		Arbitrary<T1> a1,
		Arbitrary<T2> a2,
		Arbitrary<T3> a3,
		Arbitrary<T4> a4,
		Arbitrary<T5> a5,
		Arbitrary<T6> a6,
		Arbitrary<T7> a7
	) {
		return new DefaultCombinator7<>(a1, a2, a3, a4, a5, a6, a7);
	}

	@Override
	public <T1, T2, T3, T4, T5, T6, T7, T8> Combinators.Combinator8<T1, T2, T3, T4, T5, T6, T7, T8> combine8(
		Arbitrary<T1> a1,
		Arbitrary<T2> a2,
		Arbitrary<T3> a3,
		Arbitrary<T4> a4,
		Arbitrary<T5> a5,
		Arbitrary<T6> a6,
		Arbitrary<T7> a7,
		Arbitrary<T8> a8
	) {
		return new DefaultCombinator8<>(a1, a2, a3, a4, a5, a6, a7, a8);
	}

	@Override
	public <T> Combinators.ListCombinator<T> combineList(List<Arbitrary<T>> listOfArbitraries) {
		return new DefaultListCombinator<>(listOfArbitraries);
	}

	@Override
	public <R> Arbitrary<R> combine(Function<List<Object>, R> combinator, Arbitrary<?>... arbitraries) {
		return new CombineArbitrary<>(combinator, arbitraries);
	}

}
