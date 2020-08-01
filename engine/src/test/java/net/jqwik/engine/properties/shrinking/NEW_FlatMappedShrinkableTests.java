package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

@Group
@Label("FlatMappedShrinkable")
class NEW_FlatMappedShrinkableTests {

	@Example
	void creation(@ForAll long seed) {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Function<Integer, Arbitrary<String>> flatMapper = anInt -> Arbitraries.strings().alpha().ofLength(anInt);
		Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);

		assertThat(shrinkable.distance().dimensions()).startsWith(ShrinkingDistance.of(3), ShrinkingDistance.of(3));
		assertThat(shrinkable.value()).hasSize(3);
	}

	@Group
	class Shrinking {

		@Property(tries = 50)
		void canIgnoreLeftSide(@ForAll long seed) {
			Assume.that(seed != 0L);

			Shrinkable<Integer> left = new OneStepShrinkable(4);
			Function<Integer, Arbitrary<Integer>> flatMapper = ignore -> Arbitraries.integers().between(0, 100);
			Shrinkable<Integer> shrinkable = left.flatMap(flatMapper, 1000, seed);
			Assume.that(shrinkable.value() > 3);

			TestingFalsifier<Integer> falsifier = anInt -> anInt < 3;
			int shrunkValue = shrinkToMinimal(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(3);
		}

		@Property(tries = 50)
		void canFullyShrinkAcrossJustOnRightSide(@ForAll Random random) {
			Shrinkable<Integer> left = Arbitraries.integers().between(0, 100).generator(10).next(random);
			Function<Integer, Arbitrary<Integer>> flatMapper = Arbitraries::just;
			Shrinkable<Integer> shrinkable = left.flatMap(flatMapper, 1000, 4142L);

			Assume.that(shrinkable.value() >= 3); // depends on seed

			TestingFalsifier<Integer> falsifier = anInt -> anInt < 3;
			int shrunkValue = shrinkToMinimal(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(3);
		}

		@Property(tries = 50, shrinking = ShrinkingMode.OFF)
		void shrinkingBothSidesToEnd(@ForAll long seed) {
			Assume.that(seed != 0L);

			Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(4);
			Function<Integer, Arbitrary<String>> flatMapper = anInt -> Arbitraries.strings().withCharRange('a', 'z').ofLength(anInt);
			Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);

			Falsifier<String> onlyEmptyStrings = aString -> {
				if (aString.isEmpty()) {
					return TryExecutionResult.satisfied();
				}
				return TryExecutionResult.falsified(null);
			};
			String shrunkValue = shrinkToMinimal(shrinkable, onlyEmptyStrings, null);
			assertThat(shrunkValue).isEqualTo("a");
		}

		@Property(tries = 50, shrinking = ShrinkingMode.OFF)
		void filterLeftSide(@ForAll long seed) {
			Assume.that(seed != 0L);

			Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(4);
			Function<Integer, Arbitrary<String>> flatMapper = anInt -> Arbitraries.strings().withCharRange('a', 'z').ofLength(anInt);
			Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);

			Falsifier<String> onlyEmptyStrings = aString -> {
				if (aString.length() == 2) {
					return TryExecutionResult.invalid();
				}
				if (aString.isEmpty()) {
					return TryExecutionResult.satisfied();
				}
				return TryExecutionResult.falsified(null);
			};
			String shrunkValue = shrinkToMinimal(shrinkable, onlyEmptyStrings, null);
			assertThat(shrunkValue).isEqualTo("a");
		}

		@Property(tries = 10, shrinking = ShrinkingMode.OFF)
		void filterRightSide(@ForAll long seed) {
			Assume.that(seed != 0L);

			Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(4);
			Function<Integer, Arbitrary<String>> flatMapper = anInt -> Arbitraries.strings().withCharRange('a', 'z').ofLength(anInt);
			Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);

			Falsifier<String> onlyEmptyStrings = aString -> {
				if (aString.contains("b")) {
					return TryExecutionResult.invalid();
				}
				if (aString.isEmpty()) {
					return TryExecutionResult.satisfied();
				}
				return TryExecutionResult.falsified(null);
			};
			String shrunkValue = shrinkToMinimal(shrinkable, onlyEmptyStrings, null);
			assertThat(shrunkValue).isEqualTo("a");
		}

		@Example
		void innerShrinkableIsMoreImportantWhileShrinking() {
			Shrinkable<Integer> integerShrinkable = new ShrinkableBigInteger(
				BigInteger.valueOf(5),
				Range.of(BigInteger.ONE, BigInteger.TEN),
				BigInteger.ONE
			).map(BigInteger::intValueExact);

			Function<Integer, Arbitrary<String>> flatMapper = i -> Arbitraries.strings().withCharRange('a', 'z').ofLength(i);
			Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, 42L);
			assertThat(shrinkable.value()).hasSize(5);

			TestingFalsifier<String> falsifier = aString -> aString.length() < 3;
			String shrunkValue = shrinkToMinimal(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo("aaa");
		}

		@Property(tries = 50)
		void canSimplifyOnBothSides(@ForAll long seed, @ForAll Random random) {
			Assume.that(seed != 0L);
			Shrinkable<Integer> integerShrinkable = Arbitraries.integers().generator(42).next(random);
			Function<Integer, Arbitrary<List<Integer>>> flatMapper = anInt -> Arbitraries.just(anInt).list();
			Shrinkable<List<Integer>> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);
			Assume.that(shrinkable.value().size() > 10);

			Falsifier<List<Integer>> onlyListsWithLessThan10Elements = aList -> {
				if (aList.size() < 10) {
					return TryExecutionResult.satisfied();
				}
				return TryExecutionResult.falsified(null);
			};
			List<Integer> shrunkValue = shrinkToMinimal(shrinkable, onlyListsWithLessThan10Elements, null);
			assertThat(shrunkValue).isEqualTo(asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
		}

		@Example
		void edgeCasesCanAlsoBeShrunk() {
			Arbitrary<Integer> length = Arbitraries.integers().between(0, 10);
			Arbitrary<List<String>> lists = length.flatMap(l -> Arbitraries.just("a").list().ofSize(l));

			Iterator<Shrinkable<List<String>>> edgeCases = lists.edgeCases().iterator();
			while (edgeCases.hasNext()) {
				Shrinkable<List<String>> edgeCase = edgeCases.next();
				if (edgeCase.value().isEmpty()) {
					continue;
				}

				Falsifier<List<String>> onlyEmptyLists = aList -> {
					if (aList.isEmpty()) {
						return TryExecutionResult.satisfied();
					}
					return TryExecutionResult.falsified(null);
				};
				List<String> result = shrinkToMinimal(edgeCase, onlyEmptyLists, null);
				assertThat(result).isEqualTo(asList("a"));
			}
		}

		// This test is duplicated in ShrinkingQualityProperties
		@Property(tries = 100)
		void flatMapRectangles(@ForAll Random random) {
			Arbitrary<Integer> lengths = Arbitraries.integers().between(0, 10);
			List<String> shrunkResult = falsifyThenShrink(
				lengths.flatMap(this::listsOfLength),
				random,
				falsifier(x -> !x.equals(asList("a", "b")))
			);

			assertThat(shrunkResult).containsExactly("a", "b");
		}

		private ListArbitrary<String> listsOfLength(int n) {
			return Arbitraries.of("a", "b").list().ofSize(n);
		}

	}
}
