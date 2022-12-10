package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.random.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;
import net.jqwik.testing.*;

import org.junit.jupiter.api.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingFalsifier.*;

@Group
@Label("FlatMappedShrinkable")
class FlatMappedShrinkableTests {

	@Property(tries = 5, edgeCases = EdgeCasesMode.NONE)
	void creation(@ForAll JqwikRandomState seed) {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Function<Integer, Arbitrary<String>> flatMapper = anInt -> Arbitraries.strings().alpha().ofLength(anInt);
		Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);

		assertThat(shrinkable.distance().dimensions()).startsWith(ShrinkingDistance.of(3), ShrinkingDistance.of(3));
		assertThat(shrinkable.value()).hasSize(3);
	}

	@Group
	@PropertyDefaults(tries = 50, shrinking = ShrinkingMode.OFF)
	class Shrinking {

		@Property
		void canIgnoreLeftSide(@ForAll JqwikRandomState seed) {
			Shrinkable<Integer> left = new OneStepShrinkable(4);
			Function<Integer, Arbitrary<Integer>> flatMapper = ignore -> Arbitraries.integers().between(0, 100);
			Shrinkable<Integer> shrinkable = left.flatMap(flatMapper, 1000, seed);
			Assume.that(shrinkable.value() > 3);

			TestingFalsifier<Integer> falsifier = anInt -> anInt < 3;
			int shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(3);
		}

		@Property
		void canFullyShrinkAcrossJustOnRightSide(@ForAll JqwikRandom random) {
			Shrinkable<Integer> left = Arbitraries.integers().between(0, 100).generator(10, true).next(random);
			Function<Integer, Arbitrary<Integer>> flatMapper = Arbitraries::just;
			Shrinkable<Integer> shrinkable = left.flatMap(flatMapper, 1000, SourceOfRandomness.createSeed("4142"));

			Assume.that(shrinkable.value() >= 3); // depends on seed

			TestingFalsifier<Integer> falsifier = anInt -> anInt < 3;
			int shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(3);
		}

		@Property
		void shrinkingBothSidesToEnd(@ForAll JqwikRandomState seed) {
			Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(4);
			Function<Integer, Arbitrary<String>> flatMapper = anInt -> Arbitraries.strings().withCharRange('a', 'z').ofLength(anInt);
			Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);

			Falsifier<String> onlyEmptyStrings = aString -> {
				if (aString.isEmpty()) {
					return TryExecutionResult.satisfied();
				}
				return TryExecutionResult.falsified(null);
			};
			String shrunkValue = shrink(shrinkable, onlyEmptyStrings, null);
			assertThat(shrunkValue).isEqualTo("a");
		}

		@Property
		void filterLeftSide(@ForAll JqwikRandomState seed) {
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
			String shrunkValue = shrink(shrinkable, onlyEmptyStrings, null);
			assertThat(shrunkValue).isEqualTo("a");
		}

		@Property
		void filterRightSide(@ForAll JqwikRandomState seed) {
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
			String shrunkValue = shrink(shrinkable, onlyEmptyStrings, null);
			assertThat(shrunkValue).isEqualTo("a");
		}

		@Property
		void innerShrinkableIsMoreImportantWhileShrinking(@ForAll JqwikRandomState seed) {
			Shrinkable<Integer> integerShrinkable = new ShrinkableBigInteger(
				BigInteger.valueOf(5),
				Range.of(BigInteger.ONE, BigInteger.TEN),
				BigInteger.ONE
			).map(BigInteger::intValueExact);

			Function<Integer, Arbitrary<String>> flatMapper = i -> Arbitraries.strings().withCharRange('a', 'z').ofLength(i);
			Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);
			assertThat(shrinkable.value()).hasSize(5);

			TestingFalsifier<String> falsifier = aString -> aString.length() < 3;
			String shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo("aaa");
		}

		@Property
		void canGrowOnRightSide(@ForAll JqwikRandomState seed) {
			Shrinkable<Integer> integerShrinkable = new ShrinkableBigInteger(
				BigInteger.valueOf(2),
				Range.of(BigInteger.ONE, BigInteger.TEN),
				BigInteger.ONE
			).map(BigInteger::intValueExact);

			Function<Integer, Arbitrary<String>> flatMapper = i -> Arbitraries.strings().withCharRange('a', 'z').ofLength(i);
			Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);
			assertThat(shrinkable.value()).hasSize(2);

			// Only then the falsifier condition is fulfilled
			Assume.that(shrinkable.value().chars().allMatch(c -> c >= 'f'));

			TestingFalsifier<String> falsifier = aString -> aString.chars().allMatch(c -> c < 'f');
			String shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo("f");
		}

		@Property
		void canSimplifyOnBothSides(@ForAll JqwikRandomState seed, @ForAll JqwikRandom random) {
			Shrinkable<Integer> integerShrinkable = Arbitraries.integers().generator(42, true).next(random);
			Function<Integer, Arbitrary<List<Integer>>> flatMapper = anInt -> Arbitraries.just(anInt).list();
			Shrinkable<List<Integer>> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);
			Assume.that(shrinkable.value().size() > 10);

			Falsifier<List<Integer>> onlyListsWithLessThan10Elements = aList -> {
				if (aList.size() < 10) {
					return TryExecutionResult.satisfied();
				}
				return TryExecutionResult.falsified(null);
			};
			List<Integer> shrunkValue = shrink(shrinkable, onlyListsWithLessThan10Elements, null);
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
				List<String> result = shrink(edgeCase, onlyEmptyLists, null);
				assertThat(result).isEqualTo(asList("a"));
			}
		}

		// This test is duplicated in ShrinkingQualityProperties
		@Property
		void flatMapRectangles(@ForAll JqwikRandom random) {
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
