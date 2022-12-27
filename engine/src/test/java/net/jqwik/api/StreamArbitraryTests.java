package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.edgeCases.*;
import net.jqwik.api.statistics.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.support.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class StreamArbitraryTests {

	@Example
	void stream(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
		StreamArbitrary<Integer> streamArbitrary = integerArbitrary.stream().ofMinSize(0).ofMaxSize(5);

		RandomGenerator<Stream<Integer>> generator = streamArbitrary.generator(1, true);

		assertGeneratedStream(generator.next(random));
		assertGeneratedStream(generator.next(random));
		assertGeneratedStream(generator.next(random));
		assertGeneratedStream(generator.next(random));
	}

	@Property(tries = 100)
	void filterStream(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 11);
		Arbitrary<Stream<Integer>> streamArbitrary = integerArbitrary.stream().ofMinSize(0).ofMaxSize(5)
				.filter(stream -> !stream.collect(Collectors.toList()).contains(11));

		RandomGenerator<Stream<Integer>> generator = streamArbitrary.generator(1, true);

		assertGeneratedStream(generator.next(random));
		assertGeneratedStream(generator.next(random));
		assertGeneratedStream(generator.next(random));
		assertGeneratedStream(generator.next(random));
	}

	@Example
	void uniquenessConstraint(@ForAll JqwikRandom random) {
		StreamArbitrary<Integer> listArbitrary =
				Arbitraries.integers().between(1, 1000).stream().ofMaxSize(20)
						   .uniqueElements(i -> i % 100);

		RandomGenerator<Stream<Integer>> generator = listArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, list -> {
			assertThat(isUniqueModulo(list, 100)).isTrue();
		});
	}

	@Example
	void uniquenessElements(@ForAll JqwikRandom random) {
		StreamArbitrary<Integer> listArbitrary =
				Arbitraries.integers().between(1, 1000).stream().ofMaxSize(20).uniqueElements();

		RandomGenerator<Stream<Integer>> generator = listArbitrary.generator(1000, true);

		assertAllGenerated(generator, random, list -> {
			assertThat(isUniqueModulo(list, 1000)).isTrue();
		});
	}

	@Example
	@StatisticsReport(onFailureOnly = true)
	void withSizeDistribution(@ForAll JqwikRandom random) {
		Arbitrary<Integer> integerArbitrary = Arbitraries.integers();
		StreamArbitrary<Integer> arbitrary =
			integerArbitrary.stream().ofMaxSize(100)
							.withSizeDistribution(RandomDistribution.uniform());

		RandomGenerator<Stream<Integer>> generator = arbitrary.generator(1, false);

		for (int i = 0; i < 5000; i++) {
			Stream<Integer> stream = generator.next(random).value();
			List<Integer> list = stream.collect(Collectors.toList());
			Statistics.collect(list.size());
		}

		Statistics.coverage(checker -> {
			for (int size = 0; size <= 100; size++) {
				checker.check(size).percentage(p -> p >= 0.4);
			}
		});
	}

	private boolean isUniqueModulo(Stream<Integer> stream, int modulo) {
		List<Integer> list = stream.collect(Collectors.toList());
		List<Integer> moduloList = list.stream().map(i -> {
			if (i == null) {
				return null;
			}
			return i % modulo;
		}).collect(Collectors.toList());
		return new LinkedHashSet<>(moduloList).size() == list.size();
	}

	@Group
	class GenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			Arbitrary<Stream<Integer>> arbitrary = ints.stream();
			return Arbitraries.of(arbitrary);
		}
	}

	@Group
	class EdgeCasesGeneration implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			Arbitrary<Stream<Integer>> arbitrary = ints.stream();
			return Arbitraries.of(arbitrary);
		}

		@Example
		void edgeCases() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			Arbitrary<Stream<Integer>> arbitrary = ints.stream();
			Set<Stream<Integer>> streams = collectEdgeCaseValues(arbitrary.edgeCases());
			Set<List<Integer>> lists = streams.stream()
											  .map(stream -> stream.collect(Collectors.toList()))
											  .collect(CollectorsSupport.toLinkedHashSet());
			assertThat(lists).containsExactlyInAnyOrder(
				Collections.emptyList(),
				Collections.singletonList(-10),
				Collections.singletonList(10)
			);
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).hasSize(3);
		}

		@Example
		void edgeCasesAreReportable() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			Arbitrary<Stream<Integer>> arbitrary = ints.stream();
			Set<Stream<Integer>> streams = collectEdgeCaseValues(arbitrary.edgeCases());
			assertThat(streams).allMatch(s -> s instanceof ReportableStream);
		}

		@Example
		void edgeCasesAreFilteredByUniquenessConstraints() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<Stream<Integer>> arbitrary = ints.stream().ofSize(2).uniqueElements(i -> i);
			assertThat(collectEdgeCaseValues(arbitrary.edgeCases())).isEmpty();
		}

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
		void combinationsAreFilteredByUniquenessConstraints() {
			Optional<ExhaustiveGenerator<Stream<Integer>>> optionalGenerator =
					Arbitraries.integers().between(1, 3).stream().ofMaxSize(2).uniqueElements(i -> i).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Stream<Integer>> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(13);
			assertThat(generator.map(s -> s.collect(Collectors.toList()))).containsExactlyInAnyOrder(
					asList(),
					asList(1),
					asList(2),
					asList(3),
					asList(1, 2),
					asList(1, 3),
					asList(2, 1),
					asList(2, 3),
					asList(3, 1),
					asList(3, 2)
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
		void shrinksToEmptyStreamByDefault(@ForAll JqwikRandom random) {
			StreamArbitrary<Integer> streams = Arbitraries.integers().between(1, 10).stream();
			Stream<Integer> value = falsifyThenShrink(streams, random);
			assertThat(value).isEmpty();
		}

		@Property
		void shrinkToMinSize(@ForAll JqwikRandom random, @ForAll @IntRange(min = 1, max = 20) int min) {
			StreamArbitrary<Integer> streams = Arbitraries.integers().between(1, 10).stream().ofMinSize(min);
			Stream<Integer> value = falsifyThenShrink(streams, random);
			List<Integer> list = toList(value);
			assertThat(list).hasSize(min);
			assertThat(list).containsOnly(1);
		}

		@Property
		void shrinkWithUniqueness(@ForAll JqwikRandom random, @ForAll @IntRange(min = 2, max = 10) int min) {
			StreamArbitrary<Integer> lists =
					Arbitraries.integers().between(1, 100).stream().ofMinSize(min).ofMaxSize(10)
							   .uniqueElements(i -> i);
			Stream<Integer> value = falsifyThenShrink(lists, random);
			List<Integer> list = toList(value);
			assertThat(list).hasSize(min);
			assertThat(isUniqueModulo(list.stream(), 100))
					.describedAs("%s is not unique mod 100", value)
					.isTrue();
			assertThat(list).allMatch(i -> i <= min);
		}

	}

	private void assertGeneratedStream(Shrinkable<Stream<Integer>> stream) {
		Set<Integer> set = stream.value().collect(CollectorsSupport.toLinkedHashSet());
		assertThat(set.size()).isBetween(0, 5);
		assertThat(set).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	private <T> List<T> toList(Stream<T> s) {
		return s.collect(Collectors.toList());
	}


}
