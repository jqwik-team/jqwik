package net.jqwik.properties.shrinking;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;

class ParameterListShrinkerTests {

	@Example
	void parametersAreShrunkIndividually() {
		Predicate<List<Integer>> forAllFalsifier = MockFalsifier.falsifyAll();
		Function<Integer, Arbitrary<Integer>> arbitraryProvider = index -> new IntegerArbitrary(index, 100);
		ParameterListShrinker<Integer> shrinker = new ParameterListShrinker<>(forAllFalsifier, arbitraryProvider);

		List<Integer> integerList = Arrays.asList(9, 9, 9, 9);
		ShrinkResult<List<Integer>> shrinkResult = shrinker.shrinkListElements(integerList, null);

		assertThat(shrinkResult.distanceToTarget()).isEqualTo(4);
		assertThat(shrinkResult.error()).isNotPresent();
		assertThat(shrinkResult.value()).containsExactly(0, 1, 2, 3);
	}

	@Example
	void assertionErrorOfLastShrinkingStepIsReturned() {
		AssertionError assertionError = new AssertionError("test");
		Predicate<List<Integer>> forAllFalsifier = list -> {
			throw assertionError;
		};
		Function<Integer, Arbitrary<Integer>> arbitraryProvider = index -> new IntegerArbitrary(index, 100);
		ParameterListShrinker<Integer> shrinker = new ParameterListShrinker<>(forAllFalsifier, arbitraryProvider);

		List<Integer> integerList = Arrays.asList(9, 9);
		ShrinkResult<List<Integer>> shrinkResult = shrinker.shrinkListElements(integerList, null);

		assertThat(shrinkResult.value()).containsExactly(0, 1);
		assertThat(shrinkResult.error()).isPresent();
		assertThat(shrinkResult.error().get()).isSameAs(assertionError);
	}
}
