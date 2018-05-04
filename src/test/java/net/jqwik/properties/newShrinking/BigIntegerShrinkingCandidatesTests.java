package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class BigIntegerShrinkingCandidatesTests {

	@Example
	void shrinkFrom0DoesNotShrink() {
		ShrinkingCandidates<BigInteger> shrinker = new BigIntegerShrinkingCandidates(Range.of(-10, 10).map(BigInteger::valueOf));
		assertThat(shrinker.candidatesFor(BigInteger.valueOf(0))).isEmpty();
	}

	@Example
	void shrinkFromValueOutsideRangeReturnsNothing() {
		ShrinkingCandidates<BigInteger> shrinker = new BigIntegerShrinkingCandidates(Range.of(-10, 10).map(BigInteger::valueOf));
		assertThat(shrinker.candidatesFor(BigInteger.valueOf(20))).isEmpty();
	}

	@Example
	void shrinkPositiveValueTowards0If0isInRange() {
		ShrinkingCandidates<BigInteger> shrinker = new BigIntegerShrinkingCandidates(Range.of(-10, 20).map(BigInteger::valueOf));
		Set<BigInteger> allShrunkValues = shrinker.candidatesFor(BigInteger.valueOf(10));
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
		ShrinkingCandidates<BigInteger> shrinker = new BigIntegerShrinkingCandidates(Range.of(-10, 20).map(BigInteger::valueOf));
		Set<BigInteger> allShrunkValues = shrinker.candidatesFor(BigInteger.valueOf(-10));
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
		ShrinkingCandidates<BigInteger> shrinker = new BigIntegerShrinkingCandidates(Range.of(-20, -5).map(BigInteger::valueOf));
		Set<BigInteger> allShrunkValues = shrinker.candidatesFor(BigInteger.valueOf(-10));
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
		ShrinkingCandidates<BigInteger> shrinker = new BigIntegerShrinkingCandidates(Range.of(5, 20).map(BigInteger::valueOf));
		Set<BigInteger> allShrunkValues = shrinker.candidatesFor(BigInteger.valueOf(10));
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
		ShrinkingCandidates<BigInteger> shrinker = new BigIntegerShrinkingCandidates(Range.of(0, 100).map(BigInteger::valueOf));
		Set<BigInteger> allShrunkValues = shrinker.candidatesFor(BigInteger.valueOf(90));
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
