package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class StreamArbitraryTests {

	@Example
	void stream(@ForAll Random random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		StreamArbitrary<Integer> streamArbitrary = integerArbitrary.stream().ofMinSize(0).ofMaxSize(5);

		RandomGenerator<Stream<Integer>> generator = streamArbitrary.generator(1);

		assertGeneratedStream(generator.next(random));
		assertGeneratedStream(generator.next(random));
		assertGeneratedStream(generator.next(random));
		assertGeneratedStream(generator.next(random));
	}

	@Example
	void streamEdgeCases() {
		Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
		Arbitrary<Stream<Integer>> arbitrary = ints.stream();
		Set<Stream<Integer>> streams = collectEdgeCases(arbitrary.edgeCases());
		Set<List<Integer>> lists = streams.stream().map(stream -> stream.collect(Collectors.toList())).collect(Collectors.toSet());
		assertThat(lists).containsExactlyInAnyOrder(
				Collections.emptyList(),
				Collections.singletonList(-10),
				Collections.singletonList(10)
		);
		assertThat(collectEdgeCases(arbitrary.edgeCases())).hasSize(3);
	}


	@Group
	class ExhaustiveGeneration {

		@Example
		void streamsAreCombinationsOfElementsUpToMaxLength() {
			Optional<ExhaustiveGenerator<Stream<Integer>>> optionalGenerator =
					Arbitraries.integers().between(1, 2).stream().ofMaxSize(2).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Stream<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(7);
			assertThat(generator.map(s -> s.collect(Collectors.toList()))).containsExactly(
					asList(),
					asList(1),
					asList(2),
					asList(1, 1),
					asList(1, 2),
					asList(2, 1),
					asList(2, 2)
			);
		}

		@Example
		void elementArbitraryNotExhaustive() {
			Optional<ExhaustiveGenerator<Stream<Double>>> optionalGenerator =
					Arbitraries.doubles().between(1, 10).stream().ofMaxSize(1).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

		@Example
		void tooManyCombinations() {
			Optional<ExhaustiveGenerator<Stream<Integer>>> optionalGenerator =
					Arbitraries.integers().between(1, 10).stream().ofMaxSize(10).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

	}

	@Group
	@PropertyDefaults(tries = 100)
	class Shrinking {

		@Property
		void shrinksToEmptyStreamByDefault(@ForAll Random random) {
			StreamArbitrary<Integer> lists = Arbitraries.integers().between(1, 10).stream();
			Stream<Integer> value = falsifyThenShrink(lists, random);
			assertThat(value).isEmpty();
		}

		@Property
		void shrinkToMinSize(@ForAll Random random, @ForAll @IntRange(min = 1, max = 20) int min) {
			StreamArbitrary<Integer> lists = Arbitraries.integers().between(1, 10).stream().ofMinSize(min);
			Stream<Integer> value = falsifyThenShrink(lists, random);
			List<Integer> list = toList(value);
			assertThat(list).hasSize(min);
			assertThat(list).containsOnly(1);
		}

	}

	private void assertGeneratedStream(Shrinkable<Stream<Integer>> stream) {
		Set<Integer> set = stream.value().collect(Collectors.toSet());
		assertThat(set.size()).isBetween(0, 5);
		assertThat(set).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	private <T> List<T> toList(Stream<T> s) {
		return s.collect(Collectors.toList());
	}


}
