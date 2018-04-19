package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@Group
class IntegralShrinkingTests {

	@Example
	void shrinkFrom0DoesNotShrink() {
		ShrinkCandidates<BigInteger> shrinker = new BigIntegerShrinkCandidates(BigInteger.valueOf(-10), BigInteger.valueOf(10));
		assertThat(shrinker.distance(BigInteger.valueOf(0))).isEqualTo(0);
		assertThat(shrinker.nextCandidates(BigInteger.valueOf(0))).isEmpty();
	}

	@Example
	void shrinkFromValueOutsideRangeReturnsNothing() {
		ShrinkCandidates<BigInteger> shrinker = new BigIntegerShrinkCandidates(BigInteger.valueOf(-10), BigInteger.valueOf(10));
		assertThat(shrinker.nextCandidates(BigInteger.valueOf(20))).isEmpty();
	}

	@Example
	void if0isInRangeDistanceIsAbsoluteNumber() {
		ShrinkCandidates<BigInteger> shrinker = new BigIntegerShrinkCandidates(BigInteger.valueOf(-10000), BigInteger
			.valueOf(10000));
		assertThat(shrinker.distance(BigInteger.valueOf(10000))).isEqualTo(10000);
		assertThat(shrinker.distance(BigInteger.valueOf(10))).isEqualTo(10);
		assertThat(shrinker.distance(BigInteger.valueOf(1))).isEqualTo(1);
		assertThat(shrinker.distance(BigInteger.valueOf(-10000))).isEqualTo(10000);
		assertThat(shrinker.distance(BigInteger.valueOf(-10))).isEqualTo(10);
		assertThat(shrinker.distance(BigInteger.valueOf(-1))).isEqualTo(1);
	}

	@Example
	void if0isOutsideRangeDistanceIsDistanceToShrinkTarget() {
		ShrinkCandidates<BigInteger> shrinkerAboveZero = new BigIntegerShrinkCandidates(BigInteger.valueOf(10), BigInteger
			.valueOf(100));
		assertThat(shrinkerAboveZero.distance(BigInteger.valueOf(20))).isEqualTo(10);
		assertThat(shrinkerAboveZero.distance(BigInteger.valueOf(10))).isEqualTo(0);

		ShrinkCandidates<BigInteger> shrinkerBelowZero = new BigIntegerShrinkCandidates(BigInteger.valueOf(-100), BigInteger
			.valueOf(-10));
		assertThat(shrinkerBelowZero.distance(BigInteger.valueOf(-20))).isEqualTo(10);
		assertThat(shrinkerBelowZero.distance(BigInteger.valueOf(-10))).isEqualTo(0);
	}

	@Example
	void shrinkPositiveValueTowards0If0isInRange() {
		ShrinkCandidates<BigInteger> shrinker = new BigIntegerShrinkCandidates(BigInteger.valueOf(-10), BigInteger.valueOf(20));
		Set<BigInteger> allShrunkValues = shrinker.nextCandidates(BigInteger.valueOf(10));
		assertThat(allShrunkValues)
			.containsExactlyInAnyOrder(
				BigInteger.valueOf(9), //
				BigInteger.valueOf(8), //
				BigInteger.valueOf(7), //
				BigInteger.valueOf(5), //
				BigInteger.valueOf(3), //
				BigInteger.valueOf(2), //
				BigInteger.valueOf(1), //
				BigInteger.valueOf(0) //
			);
	}

	@Example
	void shrinkNegativeValueTowards0If0isInRange() {
		ShrinkCandidates<BigInteger> shrinker = new BigIntegerShrinkCandidates(BigInteger.valueOf(-10), BigInteger.valueOf(20));
		Set<BigInteger> allShrunkValues = shrinker.nextCandidates(BigInteger.valueOf(-10));
		assertThat(allShrunkValues)
			.containsExactlyInAnyOrder(
				BigInteger.valueOf(-9), //
				BigInteger.valueOf(-8), //
				BigInteger.valueOf(-7), //
				BigInteger.valueOf(-5), //
				BigInteger.valueOf(-3), //
				BigInteger.valueOf(-2), //
				BigInteger.valueOf(-1), //
				BigInteger.valueOf(0) //
			);
	}

	@Example
	void shrinkNegativeValueTowardMaxIf0IsOutsideRange() {
		ShrinkCandidates<BigInteger> shrinker = new BigIntegerShrinkCandidates(BigInteger.valueOf(-20), BigInteger.valueOf(-5));
		Set<BigInteger> allShrunkValues = shrinker.nextCandidates(BigInteger.valueOf(-10));
		assertThat(allShrunkValues)
			.containsExactlyInAnyOrder(
				BigInteger.valueOf(-9), //
				BigInteger.valueOf(-8), //
				BigInteger.valueOf(-7), //
				BigInteger.valueOf(-6), //
				BigInteger.valueOf(-5) //
			);
	}

	@Example
	void shrinkPositiveValueTowardMinIf0IsOutsideRange() {
		ShrinkCandidates<BigInteger> shrinker = new BigIntegerShrinkCandidates(BigInteger.valueOf(5), BigInteger.valueOf(20));
		Set<BigInteger> allShrunkValues = shrinker.nextCandidates(BigInteger.valueOf(10));
		assertThat(allShrunkValues)
			.containsExactlyInAnyOrder(
				BigInteger.valueOf(9), //
				BigInteger.valueOf(8), //
				BigInteger.valueOf(7), //
				BigInteger.valueOf(6), //
				BigInteger.valueOf(5) //
			);
	}

	@Example
	void shrinkCandidatesApproachTargetAndShrinkValueWithFibbonacciDistance() {
		ShrinkCandidates<BigInteger> shrinker = new BigIntegerShrinkCandidates(BigInteger.valueOf(0), BigInteger.valueOf(100));
		Set<BigInteger> allShrunkValues = shrinker.nextCandidates(BigInteger.valueOf(90));
		assertThat(allShrunkValues)
			.containsExactlyInAnyOrder(
				BigInteger.valueOf(0), //
				BigInteger.valueOf(1), //
				BigInteger.valueOf(2), //
				BigInteger.valueOf(3), //
				BigInteger.valueOf(5), //
				BigInteger.valueOf(8), //
				BigInteger.valueOf(13), //
				BigInteger.valueOf(21), //
				BigInteger.valueOf(34), //
				BigInteger.valueOf(55), //
				BigInteger.valueOf(90 - 1), //
				BigInteger.valueOf(90 - 2), //
				BigInteger.valueOf(90 - 3), //
				BigInteger.valueOf(90 - 5), //
				BigInteger.valueOf(90 - 8), //
				BigInteger.valueOf(90 - 13), //
				BigInteger.valueOf(90 - 21), //
				BigInteger.valueOf(90 - 34), //
				BigInteger.valueOf(90 - 55) //
			);
	}

}
