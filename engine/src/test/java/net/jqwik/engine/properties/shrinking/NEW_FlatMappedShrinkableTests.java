package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.NEW_ShrinkingTestHelper.*;

@Group
@Label("FlatMappedShrinkable")
class NEW_FlatMappedShrinkableTests {

	@Example
	void creation(@ForAll long seed) {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Function<Integer, Arbitrary<String>> flatMapper = anInt -> Arbitraries.strings().alpha().ofLength(anInt);
		Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);

		assertThat(shrinkable.distance().dimensions()).startsWith(ShrinkingDistance.of(3), ShrinkingDistance.of(3));
		assertThat(shrinkable.createValue()).hasSize(3);
	}

	@Group
	class Shrinking {

		@Property(tries = 50)
		void canIgnoreLeftSide(@ForAll long seed) {
			Assume.that(seed != 0L);

			Shrinkable<Integer> left = new OneStepShrinkable(4);
			Function<Integer, Arbitrary<Integer>> flatMapper = ignore -> Arbitraries.integers().between(0, 100);
			Shrinkable<Integer> shrinkable = left.flatMap(flatMapper, 1000, seed);
			Assume.that(shrinkable.createValue() > 3);

			TestingFalsifier<Integer> falsifier = anInt -> anInt < 3;
			int shrunkValue = shrinkToEnd(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(3);
		}

		@Property(tries = 50)
		void canFullyShrinkAcrossJustOnRightSide(@ForAll Random random) {
			Shrinkable<Integer> left = Arbitraries.integers().between(0, 100).generator(10).next(random);
			Function<Integer, Arbitrary<Integer>> flatMapper = Arbitraries::just;
			Shrinkable<Integer> shrinkable = left.flatMap(flatMapper, 1000, 4142L);

			Assume.that(shrinkable.createValue() >= 3); // depends on seed

			TestingFalsifier<Integer> falsifier = anInt -> anInt < 3;
			int shrunkValue = shrinkToEnd(shrinkable, falsifier, null);
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
			String shrunkValue = shrinkToEnd(shrinkable, onlyEmptyStrings, null);
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
			String shrunkValue = shrinkToEnd(shrinkable, onlyEmptyStrings, null);
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
			String shrunkValue = shrinkToEnd(shrinkable, onlyEmptyStrings, null);
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
			assertThat(shrinkable.createValue()).hasSize(5);

			TestingFalsifier<String> falsifier = aString -> aString.length() < 3;
			String shrunkValue = shrinkToEnd(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo("aaa");
		}

	}

	// def test_can_simplify_flatmap_with_bounded_left_hand_size():
	//     assert (
	//             minimal(booleans().flatmap(lambda x: lists(just(x))), lambda x: len(x) >= 10)
	//             == [False] * 10
	//     )
	//
	//
	// def test_can_simplify_on_right_hand_strategy_of_flatmap():
	//     assert minimal(integers().flatmap(lambda x: lists(just(x)))) == []
	//
	//
	// def test_can_simplify_on_both_sides_of_flatmap():
	//     assert (
	//             minimal(integers().flatmap(lambda x: lists(just(x))), lambda x: len(x) >= 10)
	//             == [0] * 10
	//     )
	//
	//
	// def test_flatmap_rectangles():
	//     lengths = integers(min_value=0, max_value=10)
	//
	//     def lists_of_length(n):
	//         return lists(sampled_from("ab"), min_size=n, max_size=n)
	//
	//     xs = minimal(
	//         lengths.flatmap(lambda w: lists(lists_of_length(w))),
	//         lambda x: ["a", "b"] in x,
	//         settings=settings(database=None, max_examples=2000),
	//     )
	//     assert xs == [["a", "b"]]

}
