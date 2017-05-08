package net.jqwik.properties;

import static java.util.Arrays.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.assertj.core.api.*;
import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;

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
		ShrinkResult<List<Object>> shrinkingResult = shrinker.shrink(asList(1111, 2222), null);

		Assertions.assertThat(shrinkingResult.value()).containsExactly(1111, 2222);
		Assertions.assertThat(shrinkingResult.error()).isNotPresent();
	}

	@Example
	void shrinkingSingleParameterWillChooseValueWithLowestDistanceToTarget() {
		Arbitrary<Integer> integerArbitrary = Mockito.mock(Arbitrary.class);
		ShrinkableChoice<Integer> choice = aChoice(ShrinkableValue.of(0, 0),
			ShrinkableValue.of(1, 1),
			ShrinkableValue.of(100, 2)
		);
		Mockito.when(integerArbitrary.shrinkableFor(42)).thenReturn(choice);

		List<Arbitrary> arbitraries = asList(integerArbitrary);

		Function<List<Object>, Boolean> failIfLargerThanZero = intParams(params -> params[0] == 0);
		shrinker = new FalsifiedShrinker(arbitraries, failIfLargerThanZero::apply);

		ShrinkResult<List<Object>> shrinkingResult = shrinker.shrink(asList(42), null);

		Assertions.assertThat(shrinkingResult.value()).containsExactly(1);
		Assertions.assertThat(shrinkingResult.error()).isNotPresent();
	}

	@Example
	void assertionErrorDuringShrinkingIsPresentInResult() {
		Arbitrary<Integer> integerArbitrary = Mockito.mock(Arbitrary.class);
		ShrinkableChoice<Integer> shrinkTree = aChoice(ShrinkableValue.of(99, 0));
		Mockito.when(integerArbitrary.shrinkableFor(42)).thenReturn(shrinkTree);

		List<Arbitrary> arbitraries = asList(integerArbitrary);

		AssertionError assertionError = new AssertionError();
		shrinker = new FalsifiedShrinker(arbitraries, ignore -> {
					throw assertionError;
				});

		ShrinkResult<List<Object>> shrinkingResult = shrinker.shrink(asList(42), null);

		Assertions.assertThat(shrinkingResult.value()).containsExactly(99);
		Assertions.assertThat(shrinkingResult.error().get()).isSameAs(assertionError);
	}

	@Example
	void severalParametersWillBeShrinkedFirstToLast() {
		Arbitrary<Integer> a1 = Mockito.mock(Arbitrary.class);
		Mockito.when(a1.shrinkableFor(1111)).thenReturn(aChoice(
			ShrinkableValue.of(1, 1),
			ShrinkableValue.of(0, 0)
		));
		Arbitrary<Integer> a2 = Mockito.mock(Arbitrary.class);
		Mockito.when(a2.shrinkableFor(2222))
				.thenReturn(aChoice(
			ShrinkableValue.of(2, 2),
			ShrinkableValue.of(1, 1),
			ShrinkableValue.of(0, 0)
		));
		List<Arbitrary> arbitraries = asList(a1, a2);

		Function<List<Object>, Boolean> failIf1stSmallerThan2nd = intParams(params -> params[0] >= params[1]);
		shrinker = new FalsifiedShrinker(arbitraries, failIf1stSmallerThan2nd::apply);

		ShrinkResult<List<Object>> shrinkingResult = shrinker.shrink(asList(1111, 2222), null);

		Assertions.assertThat(shrinkingResult.value()).containsExactly(0, 1);
		Assertions.assertThat(shrinkingResult.error()).isNotPresent();
	}

	private Function<List<Object>, Boolean> intParams(Function<Integer[], Boolean> falsifier) {
		return params -> {
			Integer[] intParams = new Integer[params.size()];
			intParams = params.stream().map(e -> (Integer) e).collect(Collectors.toList()).toArray(intParams);
			return falsifier.apply(intParams);
		};
	}

	private ShrinkableChoice<Integer> aChoice(Shrinkable<Integer>... choices) {
		ShrinkableChoice<Integer> tree = new ShrinkableChoice<>();
		for (Shrinkable<Integer> choice : choices) {
			tree.addChoice(choice);
		}
		return tree;
	}
}
