package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;
import org.assertj.core.api.*;
import org.mockito.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Arrays.*;

class FalsifiedShrinkerTests {

	private FalsifiedShrinker shrinker;

	@Example
	void ifShrinkerDoesntProvideAnythingOriginalParametersAreReturned() {
		Arbitrary<Integer> a1 = Mockito.mock(Arbitrary.class);
		Mockito.when(a1.shrinkableFor(1111)).thenReturn(ShrinkableChoice.empty());
		Arbitrary<Integer> a2 = Mockito.mock(Arbitrary.class);
		Mockito.when(a2.shrinkableFor(2222)).thenReturn(ShrinkableChoice.empty());
		List<Arbitrary> arbitraries = asList(a1, a2);

		shrinker = new FalsifiedShrinker(arbitraries, params -> false);
		FalsifiedShrinker.Result shrinkingResult = shrinker.shrink(asList(1111, 2222), null);

		Assertions.assertThat(shrinkingResult.params()).containsExactly(1111, 2222);
		Assertions.assertThat(shrinkingResult.error()).isNull();
	}

	@Example
	void shrinkingSingleParameterWillChooseValueWithLowestDistanceToTarget() {
		Arbitrary<Integer> integerArbitrary = Mockito.mock(Arbitrary.class);
		ShrinkableChoice<Integer> shrinkTree = aShrinkTree(asList(
			ShrinkableValue.of(2, 2),
			ShrinkableValue.of(1, 1),
			ShrinkableValue.of(0, 0)
		), asList(
			ShrinkableValue.of(100, 2)
		));
		Mockito.when(integerArbitrary.shrinkableFor(42)).thenReturn(shrinkTree);

		List<Arbitrary> arbitraries = asList(integerArbitrary);

		Function<List<Object>, Boolean> failIfLargerThanZero = intParams(params -> params[0] == 0);
		shrinker = new FalsifiedShrinker(arbitraries, failIfLargerThanZero);

		FalsifiedShrinker.Result shrinkingResult = shrinker.shrink(asList(42), null);

		Assertions.assertThat(shrinkingResult.params()).containsExactly(1);
		Assertions.assertThat(shrinkingResult.error()).isNull();
	}

	@Example
	void assertionErrorDuringShrinkingIsPresentInResult() {
		Arbitrary<Integer> integerArbitrary = Mockito.mock(Arbitrary.class);
		ShrinkableChoice<Integer> shrinkTree = aShrinkTree(asList(
			ShrinkableValue.of(99, 0)
		));
		Mockito.when(integerArbitrary.shrinkableFor(42)).thenReturn(shrinkTree);

		List<Arbitrary> arbitraries = asList(integerArbitrary);

		AssertionError assertionError = new AssertionError();
		shrinker = new FalsifiedShrinker(arbitraries, ignore -> {
			throw assertionError;
		});

		FalsifiedShrinker.Result shrinkingResult = shrinker.shrink(asList(42), null);

		Assertions.assertThat(shrinkingResult.params()).containsExactly(99);
		Assertions.assertThat(shrinkingResult.error()).isSameAs(assertionError);
	}

	@Example
	void severalParametersWillBeShrinkedFirstToLast() {
		Arbitrary<Integer> a1 = Mockito.mock(Arbitrary.class);
		Mockito.when(a1.shrinkableFor(1111)).thenReturn(aShrinkTree(asList(
			ShrinkableValue.of(2, 2),
			ShrinkableValue.of(1, 1),
			ShrinkableValue.of(0, 0)
		)));
		Arbitrary<Integer> a2 = Mockito.mock(Arbitrary.class);
		Mockito.when(a2.shrinkableFor(2222)).thenReturn(aShrinkTree(asList(
			ShrinkableValue.of(2, 2),
			ShrinkableValue.of(1, 1),
			ShrinkableValue.of(0, 0)
		)));
		List<Arbitrary> arbitraries = asList(a1, a2);

		Function<List<Object>, Boolean> failIf1stSmallerThan2nd = intParams(params -> params[0] >= params[1]);
		shrinker = new FalsifiedShrinker(arbitraries, failIf1stSmallerThan2nd);

		FalsifiedShrinker.Result shrinkingResult = shrinker.shrink(asList(1111, 2222), null);

		Assertions.assertThat(shrinkingResult.params()).containsExactly(0, 1);
		Assertions.assertThat(shrinkingResult.error()).isNull();
	}

	private Function<List<Object>, Boolean> intParams(Function<Integer[], Boolean> falsifier) {
		return params -> {
			Integer[] intParams = new Integer[params.size()];
			intParams = params.stream().map(e -> (Integer) e).collect(Collectors.toList()).toArray(intParams);
			return falsifier.apply(intParams);
		};
	}

	private ShrinkableChoice<Integer> aShrinkTree(List<Shrinkable<Integer>>... routes) {
		ShrinkableChoice<Integer> tree = new ShrinkableChoice<>();
		for (List<Shrinkable<Integer>> route : routes) {
			tree.addChoice(route);
		}
		return tree;
	}
}
