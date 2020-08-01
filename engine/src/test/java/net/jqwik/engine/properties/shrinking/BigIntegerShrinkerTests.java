package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.stream.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class BigIntegerShrinkerTests {

	@Example
	void shrinkFrom0DoesNotShrink() {
		BigIntegerShrinker shrinker = new BigIntegerShrinker(BigInteger.ZERO);
		assertThat(shrinker.shrink(BigInteger.ZERO)).isEmpty();
	}

	@Example
	void shrinkPositiveValueTowards0If0isInRange() {
		BigIntegerShrinker shrinker = new BigIntegerShrinker(BigInteger.ZERO);
		Stream<BigInteger> allShrunkValues = shrinker.shrink(BigInteger.valueOf(10));
		assertThat(allShrunkValues)
			.containsExactlyInAnyOrder(
				BigInteger.valueOf(9),
				BigInteger.valueOf(8),
				BigInteger.valueOf(7),
				BigInteger.valueOf(5),
				BigInteger.valueOf(3),
				BigInteger.valueOf(2),
				BigInteger.valueOf(1),
				BigInteger.ZERO
			);
	}

	@Example
	void shrinkNegativeValueTowards0If0isInRange() {
		BigIntegerShrinker shrinker = new BigIntegerShrinker(BigInteger.ZERO);
		Stream<BigInteger> allShrunkValues = shrinker.shrink(BigInteger.valueOf(-10));
		assertThat(allShrunkValues)
			.containsExactlyInAnyOrder(
				BigInteger.valueOf(-9),
				BigInteger.valueOf(-8),
				BigInteger.valueOf(-7),
				BigInteger.valueOf(-5),
				BigInteger.valueOf(-3),
				BigInteger.valueOf(-2),
				BigInteger.valueOf(-1),
				BigInteger.ZERO
			);
	}

	@Example
	void shrinkNegativeValueTowardMaxIf0IsOutsideRange() {
		BigIntegerShrinker shrinker = new BigIntegerShrinker(BigInteger.valueOf(-5));
		Stream<BigInteger> allShrunkValues = shrinker.shrink(BigInteger.valueOf(-10));
		assertThat(allShrunkValues)
			.containsExactlyInAnyOrder(
				BigInteger.valueOf(-9),
				BigInteger.valueOf(-8),
				BigInteger.valueOf(-7),
				BigInteger.valueOf(-6),
				BigInteger.valueOf(-5)
			);
	}

	@Example
	void shrinkPositiveValueTowardMinIf0IsOutsideRange() {
		BigIntegerShrinker shrinker = new BigIntegerShrinker(BigInteger.valueOf(5));
		Stream<BigInteger> allShrunkValues = shrinker.shrink(BigInteger.valueOf(10));
		assertThat(allShrunkValues)
			.containsExactlyInAnyOrder(
				BigInteger.valueOf(9),
				BigInteger.valueOf(8),
				BigInteger.valueOf(7),
				BigInteger.valueOf(6),
				BigInteger.valueOf(5)
			);
	}

	@Example
	void shrinkCandidatesApproachTargetAndShrinkValueWithFibbonacciDistance() {
		BigIntegerShrinker shrinker = new BigIntegerShrinker(BigInteger.ZERO);
		Stream<BigInteger> allShrunkValues = shrinker.shrink(BigInteger.valueOf(90));
		assertThat(allShrunkValues)
			.containsExactlyInAnyOrder(
				BigInteger.ZERO,
				BigInteger.valueOf(1),
				BigInteger.valueOf(2),
				BigInteger.valueOf(3),
				BigInteger.valueOf(5),
				BigInteger.valueOf(8),
				BigInteger.valueOf(13),
				BigInteger.valueOf(21),
				BigInteger.valueOf(34),
				BigInteger.valueOf(55),
				BigInteger.valueOf(90 - 1),
				BigInteger.valueOf(90 - 2),
				BigInteger.valueOf(90 - 3),
				BigInteger.valueOf(90 - 5),
				BigInteger.valueOf(90 - 8),
				BigInteger.valueOf(90 - 13),
				BigInteger.valueOf(90 - 21),
				BigInteger.valueOf(90 - 34),
				BigInteger.valueOf(90 - 55)
			);
	}

}
