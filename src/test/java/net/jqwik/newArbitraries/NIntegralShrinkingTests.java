package net.jqwik.newArbitraries;

import static net.jqwik.newArbitraries.NArbitraryTestHelper.*;
import static org.assertj.core.api.Assertions.*;

import java.util.*;

import net.jqwik.api.*;

class NIntegralShrinkingTests {

	@Group
	class Integers {

		@Example
		void shrinkFrom0DoesNotShrink() {
			NShrinker<Integer> shrinker = new NIntegerShrinker(-10, 10);
			assertThat(shrinker.distance(0)).isEqualTo(0);
			assertThat(shrinker.shrink(0)).isEmpty();
		}

		@Example
		void shrinkFromValueOutsideRangeReturnsNothing() {
			NShrinker<Integer> shrinker = new NIntegerShrinker(-10, 10);
			assertThat(shrinker.shrink(20)).isEmpty();
		}

		@Example
		void if0isInRangeDistanceIsAbsoluteNumber() {
			NShrinker<Integer> shrinker = new NIntegerShrinker(-10000, 10000);
			assertThat(shrinker.distance(10000)).isEqualTo(10000);
			assertThat(shrinker.distance(10)).isEqualTo(10);
			assertThat(shrinker.distance(1)).isEqualTo(1);
			assertThat(shrinker.distance(-10000)).isEqualTo(10000);
			assertThat(shrinker.distance(-10)).isEqualTo(10);
			assertThat(shrinker.distance(-1)).isEqualTo(1);
		}

		@Example
		void if0isOutsideRangeDistanceIsDistanceToShrinkTarget() {
			NShrinker<Integer> shrinkerAboveZero = new NIntegerShrinker(10, 100);
			assertThat(shrinkerAboveZero.distance(20)).isEqualTo(10);
			assertThat(shrinkerAboveZero.distance(10)).isEqualTo(0);

			NShrinker<Integer> shrinkerBelowZero = new NIntegerShrinker(-100, -10);
			assertThat(shrinkerBelowZero.distance(-20)).isEqualTo(10);
			assertThat(shrinkerBelowZero.distance(-10)).isEqualTo(0);
		}

		@Example
		void shrinkPositiveValueTowards0If0isInRange() {
			NShrinker<Integer> shrinker = new NIntegerShrinker(-10, 20);
			List<Integer> allShrunkValues = shrinkToEnd(shrinker, 10);
			assertThat(allShrunkValues).containsExactly(5, 3, 2, 1, 0);
		}

		@Example
		void shrinkNegativeValueTowards0If0isInRange() {
			NShrinker<Integer> shrinker = new NIntegerShrinker(-10, 20);
			List<Integer> allShrunkValues = shrinkToEnd(shrinker, -10);
			assertThat(allShrunkValues).containsExactly(-5, -3, -2, -1, 0);
		}

		@Example
		void shrinkNegativeValueTowardMaxIf0IsOutsideRange() {
			NShrinker<Integer> shrinker = new NIntegerShrinker(-20, -5);
			List<Integer> allShrunkValues = shrinkToEnd(shrinker, -10);
			assertThat(allShrunkValues).containsExactly(-8, -7, -6, -5);
		}

		@Example
		void shrinkPositiveValueTowardMinIf0IsOutsideRange() {
			NShrinker<Integer> shrinker = new NIntegerShrinker(5, 20);
			List<Integer> allShrunkValues = shrinkToEnd(shrinker, 10);
			assertThat(allShrunkValues).containsExactly(8, 7, 6, 5);
		}

	}

	@Group
	class Longs {

		@Example
		void distanceOfLongIsIntMaxIfLongLargerThanIntMax() {
			NShrinker<Long> shrinker = new NLongShrinker(Long.MIN_VALUE, Long.MAX_VALUE);
			assertThat(shrinker.distance(10000L)).isEqualTo(10000);
			assertThat(shrinker.distance(-10000L)).isEqualTo(10000);
			assertThat(shrinker.distance(100_000_000_000L)).isEqualTo(Integer.MAX_VALUE);
			assertThat(shrinker.distance(-100_000_000_000L)).isEqualTo(Integer.MAX_VALUE);
		}

		@Example
		void longsAreShrunkEvenAboveIntMax() {
			NShrinker<Long> shrinker = new NLongShrinker(Long.MIN_VALUE, Long.MAX_VALUE);
			assertThat(shrinker.shrink(128_000_000_000L)).containsExactly(64_000_000_000L);
//			List<Long> allShrunkValues = shrinkToEnd(shrinker, 128_000_000_000L);
//			assertThat(allShrunkValues).startsWith( //
//					64_000_000_000L, //
//					32_000_000_000L, //
//					16_000_000_000L, //
//					8_000_000_000L, //
//					4_000_000_000L, //
//					2_000_000_000L, //
//					1_000_000_000L, //
//					500_000_000L, //
//					250_000_000L, //
//					125_000_000L //
//			);
//
		}

	}

}
