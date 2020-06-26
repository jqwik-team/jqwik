package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ArbitraryTestHelper.*;

@Group
class StreamableArbitraryTests {

	@Group
	class Lists {

		@Example
		void list() {
			Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
			Arbitrary<List<String>> listArbitrary = stringArbitrary.list();

			RandomGenerator<List<String>> generator = listArbitrary.generator(1);
			assertGeneratedLists(generator, 0, Integer.MAX_VALUE);
		}

		@Example
		void ofSize() {
			Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
			Arbitrary<List<String>> listArbitrary = stringArbitrary.list().ofSize(42);

			RandomGenerator<List<String>> generator = listArbitrary.generator(1);
			assertGeneratedLists(generator, 42, 42);
		}

		@Example
		void ofMinSize_ofMaxSize() {
			Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");
			Arbitrary<List<String>> listArbitrary = stringArbitrary.list().ofMinSize(2).ofMaxSize(5);

			RandomGenerator<List<String>> generator = listArbitrary.generator(1);
			assertGeneratedLists(generator, 2, 5);
		}

		@Example
		void reduceList() {
			StreamableArbitrary<Integer, List<Integer>> streamableArbitrary =
				Arbitraries.integers().between(1, 5).list().ofMinSize(1).ofMaxSize(10);

			Arbitrary<Integer> integerArbitrary = streamableArbitrary.reduce(0, Integer::sum);

			RandomGenerator<Integer> generator = integerArbitrary.generator(1000);

			assertAllGenerated(generator, sum -> {
				assertThat(sum).isBetween(1, 50);
			});

			assertAtLeastOneGenerated(generator, sum -> sum == 1);
			assertAtLeastOneGenerated(generator, sum -> sum > 30);
		}

		@Example
		void mapEach() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
			Arbitrary<List<Tuple2<Integer, List<Integer>>>> setArbitrary =
				integerArbitrary
					.list().ofSize(5)
					.mapEach((all, each) -> Tuple.of(each, all));

			RandomGenerator<List<Tuple2<Integer, List<Integer>>>> generator = setArbitrary.generator(1);

			assertAllGenerated(generator, set -> {
				assertThat(set).hasSize(5);
				assertThat(set).allMatch(tuple -> tuple.get2().size() == 5);
			});
		}

		@Example
		void flatMapEach() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
			Arbitrary<List<Tuple2<Integer, Integer>>> setArbitrary =
				integerArbitrary
					.list().ofSize(5)
					.flatMapEach((all, each) ->
									 Arbitraries.of(all).map(friend -> Tuple.of(each, friend))
					);

			RandomGenerator<List<Tuple2<Integer, Integer>>> generator = setArbitrary.generator(1);

			assertAllGenerated(generator, set -> {
				assertThat(set).hasSize(5);
				assertThat(set).allMatch(tuple -> tuple.get2() <= 10);
			});
		}

		private void assertGeneratedLists(RandomGenerator<List<String>> generator, int minSize, int maxSize) {
			assertAllGenerated(generator, list -> {
				assertThat(list.size()).isBetween(minSize, maxSize);
				assertThat(list).isSubsetOf("1", "hallo", "test");
			});
		}

	}

	@Group
	class Sets {

		@Example
		void set() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
			SetArbitrary<Integer> setArbitrary = integerArbitrary.set().ofMinSize(2).ofMaxSize(7);

			RandomGenerator<Set<Integer>> generator = setArbitrary.generator(1);

			assertGeneratedSet(generator, 2, 7);
		}

		@Example
		void setWithLessElementsThanMaxSize() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.of(1, 2, 3, 4, 5);
			SetArbitrary<Integer> setArbitrary = integerArbitrary.set().ofMinSize(2);

			RandomGenerator<Set<Integer>> generator = setArbitrary.generator(1);

			assertGeneratedSet(generator, 2, 5);
		}

		@Example
		void mapEach() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
			Arbitrary<Set<Tuple2<Integer, Set<Integer>>>> setArbitrary =
				integerArbitrary
					.set().ofSize(5)
					.mapEach((all, each) -> Tuple.of(each, all));

			RandomGenerator<Set<Tuple2<Integer, Set<Integer>>>> generator = setArbitrary.generator(1);

			assertAllGenerated(generator, set -> {
				assertThat(set).hasSize(5);
				assertThat(set).allMatch(tuple -> tuple.get2().size() == 5);
			});
		}

		@Example
		void flatMapEach() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
			Arbitrary<Set<Tuple2<Integer, Integer>>> setArbitrary =
				integerArbitrary
					.set().ofSize(5)
					.flatMapEach((all, each) ->
									 Arbitraries.of(all).map(friend -> Tuple.of(each, friend))
					);

			RandomGenerator<Set<Tuple2<Integer, Integer>>> generator = setArbitrary.generator(1);

			assertAllGenerated(generator, set -> {
				assertThat(set).hasSize(5);
				assertThat(set).allMatch(tuple -> tuple.get2() <= 10);
			});
		}

		private void assertGeneratedSet(RandomGenerator<Set<Integer>> generator, int minSize, int maxSize) {
			assertAllGenerated(generator, set -> {
				assertThat(set.size()).isBetween(minSize, maxSize);
				assertThat(set).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
			});
		}

	}

	@Group
	class Streams {

		@Example
		void stream(@ForAll Random random) {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
			Arbitrary<Stream<Integer>> streamArbitrary = integerArbitrary.stream().ofMinSize(0).ofMaxSize(5);

			RandomGenerator<Stream<Integer>> generator = streamArbitrary.generator(1);

			assertGeneratedStream(generator.next(random));
			assertGeneratedStream(generator.next(random));
			assertGeneratedStream(generator.next(random));
			assertGeneratedStream(generator.next(random));
		}

		private void assertGeneratedStream(Shrinkable<Stream<Integer>> stream) {
			Set<Integer> set = stream.value().collect(Collectors.toSet());
			assertThat(set.size()).isBetween(0, 5);
			assertThat(set).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		}

	}

	@Group
	class Arrays {

		@Example
		void array() {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
			Arbitrary<Integer[]> arrayArbitrary = integerArbitrary.array(Integer[].class).ofMinSize(2).ofMaxSize(5);

			RandomGenerator<Integer[]> generator = arrayArbitrary.generator(1);

			assertAllGenerated(generator, array -> {
				assertThat(array.length).isBetween(2, 5);
				assertThat(array).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
			});
		}

		@Example
		void reduceArray() {
			StreamableArbitrary<Integer, Integer[]> streamableArbitrary =
				Arbitraries.integers().between(1, 5).array(Integer[].class).ofMinSize(1).ofMaxSize(10);

			Arbitrary<Integer> integerArbitrary = streamableArbitrary.reduce(0, Integer::sum);

			RandomGenerator<Integer> generator = integerArbitrary.generator(1000);

			assertAllGenerated(generator, sum -> {
				assertThat(sum).isBetween(1, 50);
			});

			assertAtLeastOneGenerated(generator, sum -> sum == 1);
			assertAtLeastOneGenerated(generator, sum -> sum > 30);
		}

		@Example
		void arrayOfPrimitiveType(@ForAll Random random) {
			Arbitrary<Integer> integerArbitrary = Arbitraries.integers().between(1, 10);
			Arbitrary<int[]> arrayArbitrary = integerArbitrary.array(int[].class).ofMinSize(0).ofMaxSize(5);

			RandomGenerator<int[]> generator = arrayArbitrary.generator(1);

			Shrinkable<int[]> array = generator.next(random);
			assertThat(array.value().length).isBetween(0, 5);
			List<Integer> actual = IntStream.of(array.value()).boxed().collect(Collectors.toList());
			assertThat(actual).isSubsetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		}

	}

}