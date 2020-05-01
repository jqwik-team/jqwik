package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.engine.properties.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ArbitraryTestHelper.*;

class ShrinkingSuggestionsTests {

	@Example
	void values() {
		Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3, 4);

		Shrinkable<Integer> shrinkable = generateValue(arbitrary, 3);

		Stream<Integer> suggestedValues = suggestedValues(shrinkable);
		assertThat(suggestedValues).containsExactly(1, 2);
	}

	@Example
	void integers() {
		Arbitrary<Integer> arbitrary = Arbitraries.integers().between(1, 6);

		Shrinkable<Integer> shrinkable = generateValue(arbitrary, 3);

		Stream<Integer> suggestedValues = suggestedValues(shrinkable);
		assertThat(suggestedValues).containsExactly(1, 2);
	}

	@Example
	void strings() {
		Arbitrary<String> arbitrary = Arbitraries.strings()
												 .withCharRange('a', 'd')
												 .ofMinLength(1)
												 .ofMaxLength(2);

		Shrinkable<String> shrinkable = generateValue(arbitrary, "bb");

		Stream<String> suggestedValues = suggestedValues(shrinkable);
		assertThat(suggestedValues).containsOnly("a", "b");

		// TODO: Improve ShrinkableContainer.shrinkingSuggestions() so that:
		// assertThat(suggestedValues).containsOnly("aa", "ab", "ba", "a", "b");
	}

	@Example
	void filtered() {
		Arbitrary<Integer> arbitrary =
			Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter(i -> i % 2 == 0);

		Shrinkable<Integer> shrinkable = generateValue(arbitrary, 6);

		Stream<Integer> suggestedValues = suggestedValues(shrinkable);
		assertThat(suggestedValues).containsExactly(2, 4);

	}

	@Example
	void mapped() {
		Arbitrary<String> arbitrary =
			Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map(String::valueOf);

		Shrinkable<String> shrinkable = generateValue(arbitrary, "3");

		Stream<String> suggestedValues = suggestedValues(shrinkable);
		assertThat(suggestedValues).containsExactly("1", "2");

	}

	@Example
	void flatMapped() {
		Arbitrary<Integer> arbitrary =
			Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
					   .flatMap(i -> Arbitraries.of(i));
		Shrinkable<Integer> shrinkable = generateValue(arbitrary, 3);

		Stream<Integer> suggestedValues = suggestedValues(shrinkable);
		assertThat(suggestedValues).containsOnly(1, 2);
	}

	@Property(tries = 10)
	void unique(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).unique();

		RandomGenerator<Integer> generator = arbitrary.generator(1000);

		Set<Integer> generated = new HashSet<>();
		generated.add(generator.next(random).value());
		generated.add(generator.next(random).value());
		generated.add(generator.next(random).value());

		Shrinkable<Integer> shrinkable = generator.next(random);

		List<Integer> expectedValues = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
		expectedValues.removeIf(value -> value >= shrinkable.value());
		expectedValues.removeIf(value -> generated.contains(value));

		Stream<Integer> suggestedValues = suggestedValues(shrinkable);
		assertThat(suggestedValues).containsExactlyElementsOf(expectedValues);
	}

	@Example
	void lazy() {
		Arbitrary<Integer> arbitrary =
			Arbitraries.lazy(() -> Arbitraries.of(1, 2, 3, 4, 5, 6));

		Shrinkable<Integer> shrinkable = generateValue(arbitrary, 3);

		Stream<Integer> suggestedValues = suggestedValues(shrinkable);
		assertThat(suggestedValues).containsOnly(1, 2);
	}

	@Example
	void combinations() {
		Arbitrary<Integer> arbitrary1 = Arbitraries.of(1, 2, 3, 4);
		Arbitrary<Integer> arbitrary2 = Arbitraries.of(1, 2, 3, 4);

		Arbitrary<Tuple2<Integer, Integer>> arbitrary =
			Combinators.combine(arbitrary1, arbitrary2).as(Tuple::of);

		Shrinkable<Tuple2<Integer, Integer>> shrinkable = generateValue(arbitrary, Tuple.of(3, 2));

		Stream<Tuple2<Integer, Integer>> suggestedValues = suggestedValues(shrinkable);

		assertThat(suggestedValues).containsExactlyInAnyOrder(
			Tuple.of(1, 1),
			Tuple.of(1, 2)
		);

		// TODO: Implement CombinedShrinkable.shrinkingSuggestions() so that:
		//		assertThat(suggestedValues).containsOnly(
		//			Tuple.of(1, 1),
		//			Tuple.of(2, 1),
		//			Tuple.of(3, 1),
		//			Tuple.of(1, 2),
		//			Tuple.of(2, 2)
		//		);
		// But maybe this will all be unnecessary when shrinking is redone :-/

	}

	private <T> Stream<T> suggestedValues(Shrinkable<T> shrinkable) {
		List<Shrinkable<T>> suggestions = shrinkable.shrinkingSuggestions();
		return suggestions.stream().map(Shrinkable::value);
	}

	private <T> Shrinkable<T> generateValue(Arbitrary<T> arbitrary, T target) {
		RandomGenerator<T> generator = arbitrary.generator(100);
		return generateUntil(generator, SourceOfRandomness.current(), value -> value.equals(target));
	}

}
