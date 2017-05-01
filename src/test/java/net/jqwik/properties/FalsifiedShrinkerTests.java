package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;
import org.assertj.core.api.*;
import org.mockito.*;

import java.util.*;
import java.util.function.*;

import static java.util.Arrays.*;

class FalsifiedShrinkerTests {

	private FalsifiedShrinker shrinker;

	@Example
	void ifShrinkerDoesntProvideAnythingOriginalParametersAreReturned() {
		Arbitrary<Integer> a1 = Mockito.mock(Arbitrary.class);
		Mockito.when(a1.shrink(1111)).thenReturn(ShrinkTree.empty());
		Arbitrary<Integer> a2 = Mockito.mock(Arbitrary.class);
		Mockito.when(a2.shrink(2222)).thenReturn(ShrinkTree.empty());
		List<Arbitrary> arbitraries = asList(a1, a2);

		Function<List<Object>, Boolean> alwaysFail = params -> false;
		shrinker = new FalsifiedShrinker(arbitraries, alwaysFail);

		FalsifiedShrinker.Result shrinkingResult = shrinker.shrink(asList(1111, 2222), null);

		Assertions.assertThat(shrinkingResult.params()).containsExactly(1111, 2222);
		Assertions.assertThat(shrinkingResult.error()).isNull();
	}

	@Example
	void shrinkingSingleParameterWillChooseValueWithLowestDistanceToTarget() {
		Arbitrary<Integer> integerArbitrary = Mockito.mock(Arbitrary.class);
		ShrinkTree<Integer> shrinkTree = ShrinkTree.empty();
		shrinkTree.addRoute(asList(
			ShrinkValue.of(2, 2),
			ShrinkValue.of(1, 1),
			ShrinkValue.of(0, 0)
		));
		shrinkTree.addRoute(asList(
			ShrinkValue.of(100, 2)
		));
		Mockito.when(integerArbitrary.shrink(42)).thenReturn(shrinkTree);

		List<Arbitrary> arbitraries = asList(integerArbitrary);

		Function<List<Object>, Boolean> failIfLargerThanZero = params -> ((int) params.get(0)) == 0;
		shrinker = new FalsifiedShrinker(arbitraries, failIfLargerThanZero);

		FalsifiedShrinker.Result shrinkingResult = shrinker.shrink(asList(42), null);

		Assertions.assertThat(shrinkingResult.params()).containsExactly(1);
		Assertions.assertThat(shrinkingResult.error()).isNull();
	}

	@Example
	void severalParametersWillBeShrinkedFirstToLast() {
		Assertions.fail("Not implemented yet");
	}
}
