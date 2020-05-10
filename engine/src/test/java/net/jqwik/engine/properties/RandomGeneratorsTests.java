package net.jqwik.engine.properties;

import java.math.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

import static java.math.BigInteger.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ArbitraryTestHelper.*;

class RandomGeneratorsTests {

	@Example
	void setsAreGeneratedWithCorrectMinAndMaxSize() {
		RandomGenerator<Integer> integerGenerator = RandomGenerators.integers(1, 10);
		RandomGenerator<Set<Integer>> generator = RandomGenerators.set(integerGenerator, 2, 5);
		ArbitraryTestHelper.assertAllGenerated(generator, set -> set.size() >= 2 && set.size() <= 5);
	}

	@Example
	void setGenerationShouldStopWithTooManyMisses(@ForAll Random random) {
		RandomGenerator<Integer> integerGenerator = RandomGenerators.integers(1, 10);
		RandomGenerator<Set<Integer>> generator = RandomGenerators.set(integerGenerator, 11, 11);

		Assertions.assertThatThrownBy(() -> generator.next(random))
				  .isInstanceOf(JqwikException.class);
	}

	@Group
	class IntegralGeneration {

		@Example
		void withinIntegerRange() {
			BigInteger min = valueOf(Integer.MIN_VALUE);
			BigInteger max = valueOf(Integer.MAX_VALUE);
			RandomGenerator<BigInteger> generator =
				RandomGenerators
					.bigIntegers(
						min,
						max,
						RandomIntegralGenerators.defaultShrinkingTarget(Range.of(min, max)),
						RandomDistribution.uniform()
					);
			assertAllWithinRange(generator, min, max);
		}

		@Example
		void withinSmallRange() {
			BigInteger min = valueOf(-100);
			BigInteger max = valueOf(10000);
			RandomGenerator<BigInteger> generator =
				RandomGenerators
					.bigIntegers(
						min,
						max,
						RandomIntegralGenerators.defaultShrinkingTarget(Range.of(min, max)),
						RandomDistribution.uniform()
					);
			assertAllWithinRange(generator, min, max);
		}

		@Example
		void withinGreaterRange() {
			BigInteger min = valueOf(-100_000_000_000L);
			BigInteger max = valueOf(100_000_000_000L);
			RandomGenerator<BigInteger> generator =
				RandomGenerators
					.bigIntegers(
						min,
						max,
						RandomIntegralGenerators.defaultShrinkingTarget(Range.of(min, max)),
						RandomDistribution.uniform()
					);
			assertAllWithinRange(generator, min, max);
		}

		@Example
		void smallRangeWithBiasedDistribution() {
			BigInteger min = valueOf(-100);
			BigInteger max = valueOf(100000);
			RandomGenerator<BigInteger> generator = RandomGenerators.bigIntegers(
				min,
				max,
				RandomIntegralGenerators.defaultShrinkingTarget(Range.of(min, max)),
				RandomDistribution.biased()
			);

			assertAllWithinRange(generator, min, max);
		}

		@Example
		void greaterRangeWithPartitions() {
			BigInteger min = valueOf(Long.MIN_VALUE);
			BigInteger max = valueOf(Long.MAX_VALUE);
			RandomGenerator<BigInteger> generator = RandomGenerators.bigIntegers(
				min,
				max,
				RandomIntegralGenerators.defaultShrinkingTarget(Range.of(min, max)),
				RandomDistribution.biased()
			);
			assertAllWithinRange(generator, min, max);
		}

		@Example
		void outsideLongRange() {
			BigInteger min = new BigInteger("-10000000000000000000");
			BigInteger max = new BigInteger("10000000000000000000");
			RandomGenerator<BigInteger> generator =
				RandomGenerators
					.bigIntegers(
						min,
						max,
						RandomIntegralGenerators.defaultShrinkingTarget(Range.of(min, max)),
						RandomDistribution.uniform()
					);
			assertAllWithinRange(generator, min, max);
			assertAtLeastOneGenerated(
				generator,
				bigInteger -> bigInteger.compareTo(valueOf(Long.MAX_VALUE)) > 0
			);
			assertAtLeastOneGenerated(
				generator,
				bigInteger -> bigInteger.compareTo(valueOf(Long.MIN_VALUE)) < 0
			);
		}

		@Example
		void minGreaterThanMaxFails() {
			assertThatThrownBy(() -> {
				BigInteger min = valueOf(1);
				BigInteger max = valueOf(-1);
				RandomGenerators
					.bigIntegers(
						min,
						max,
						RandomIntegralGenerators.defaultShrinkingTarget(Range.of(min, max)),
						RandomDistribution.uniform()
					);
			}).isInstanceOf(IllegalArgumentException.class);
		}

	}

	@Group
	class BigDecimalGeneration {

		@Example
		void smalls() {
			BigDecimal min = new BigDecimal(-10);
			BigDecimal max = new BigDecimal(10);
			Range<BigDecimal> range = Range.of(min, max);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators
					.bigDecimals(range, 2, RandomDecimalGenerators.defaultShrinkingTarget(range, 2), RandomDistribution.uniform());
			ArbitraryTestHelper.assertAllGenerated(
				generator,
				decimal -> decimal.compareTo(min) >= 0 && decimal.compareTo(max) <= 0 && decimal.scale() == 2
			);
		}

		@Example
		void bordersExcluded() {
			BigDecimal min = new BigDecimal(-10);
			BigDecimal max = new BigDecimal(10);
			Range<BigDecimal> range = Range.of(min, false, max, false);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators
					.bigDecimals(range, 1, RandomDecimalGenerators.defaultShrinkingTarget(range, 1), RandomDistribution.uniform());
			ArbitraryTestHelper.assertAllGenerated(
				generator,
				decimal -> decimal.compareTo(min) > 0 && decimal.compareTo(max) < 0 && decimal.scale() == 1
			);
		}

		@Example
		void bordersExcludedAllPositive() {
			BigDecimal min = new BigDecimal(1);
			BigDecimal max = new BigDecimal(10);
			Range<BigDecimal> range = Range.of(min, false, max, false);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators
					.bigDecimals(range, 1, RandomDecimalGenerators.defaultShrinkingTarget(range, 1), RandomDistribution.uniform());
			ArbitraryTestHelper.assertAllGenerated(
				generator,
				decimal -> decimal.compareTo(min) > 0 && decimal.compareTo(max) < 0 && decimal.scale() == 1
			);
		}

		@Example
		void bordersExcludedAllNegative() {
			BigDecimal min = new BigDecimal(-10);
			BigDecimal max = new BigDecimal(-1);
			Range<BigDecimal> range = Range.of(min, false, max, false);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators
					.bigDecimals(range, 1, RandomDecimalGenerators.defaultShrinkingTarget(range, 1), RandomDistribution.uniform());
			ArbitraryTestHelper.assertAllGenerated(
				generator,
				decimal -> decimal.compareTo(min) > 0 && decimal.compareTo(max) < 0 && decimal.scale() == 1
			);
		}

		@Example
		void smallRange() {
			BigDecimal min = new BigDecimal("0.01");
			BigDecimal max = new BigDecimal("0.03");
			Range<BigDecimal> range = Range.of(min, false, max, false);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators
					.bigDecimals(range, 2, RandomDecimalGenerators.defaultShrinkingTarget(range, 2), RandomDistribution.uniform());
			ArbitraryTestHelper.assertAllGenerated(
				generator,
				decimal -> { return decimal.equals(new BigDecimal("0.02"));}
			);
		}

		@Example
		void impossibleRange() {
			BigDecimal min = new BigDecimal(0);
			BigDecimal max = new BigDecimal(1);
			Range<BigDecimal> range = Range.of(min, false, max, false);
			assertThatThrownBy(
				() -> RandomGenerators
						  .bigDecimals(range, 0, RandomDecimalGenerators.defaultShrinkingTarget(range, 0), RandomDistribution.uniform())
			).isInstanceOf(JqwikException.class);
		}

		@Example
		void bigBigDecimals() {
			BigDecimal min = BigDecimal.valueOf(-Double.MAX_VALUE);
			BigDecimal max = BigDecimal.valueOf(Double.MAX_VALUE);
			Range<BigDecimal> range = Range.of(min, max);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators
					.bigDecimals(range, 0, RandomDecimalGenerators.defaultShrinkingTarget(range, 0), RandomDistribution.uniform());
			ArbitraryTestHelper.assertAllGenerated(generator, decimal -> {
				assertThat(decimal).isBetween(min, max);
				assertThat(decimal.scale()).isEqualTo(0);
			});
		}

		@Example
		void smallRangeWithBiasedDistribution() {
			Range<BigDecimal> range = Range.of(BigDecimal.valueOf(-100), BigDecimal.valueOf(100000));
			RandomGenerator<BigInteger> generator =
				RandomGenerators.bigDecimals(
					range, 0,
					RandomDecimalGenerators.defaultShrinkingTarget(range, 0),
					RandomDistribution.biased()
				).map(BigDecimal::toBigInteger);

			assertAllWithinRange(generator, BigDecimal.valueOf(-100).toBigInteger(), BigDecimal.valueOf(100000).toBigInteger());
			assertAllPartitionsAreCovered(generator, BigDecimal.valueOf(-100).toBigInteger(), BigDecimal.valueOf(100000).toBigInteger(),
										  Arrays.asList(BigInteger.ZERO, valueOf(100), valueOf(1000))
			);
		}

		@Example
		void greaterRangeWithBiasedDistribution() {
			Range<BigDecimal> range = Range.of(BigDecimal.valueOf(Long.MIN_VALUE), BigDecimal.valueOf(Long.MAX_VALUE));
			RandomGenerator<BigInteger> generator =
				RandomGenerators.bigDecimals(
					range, 0,
					RandomDecimalGenerators.defaultShrinkingTarget(range, 0),
					RandomDistribution.biased()
				).map(BigDecimal::toBigInteger);

			assertAllWithinRange(
				generator,
				BigDecimal.valueOf(Long.MIN_VALUE).toBigInteger(),
				BigDecimal.valueOf(Long.MAX_VALUE).toBigInteger()
			);
			assertAllPartitionsAreCovered(
				generator,
				BigDecimal.valueOf(Long.MIN_VALUE).toBigInteger(),
				BigDecimal.valueOf(Long.MAX_VALUE).toBigInteger(),
				Arrays.asList(BigInteger.ZERO, valueOf(-10000), valueOf(10000))
			);
		}

		@Example
		void minGreaterThanMaxFails() {
			assertThatThrownBy(() -> {
				Range<BigDecimal> range = Range.of(BigDecimal.valueOf(1), BigDecimal.valueOf(-1));
				RandomGenerators
					.bigDecimals(range, 2, RandomDecimalGenerators.defaultShrinkingTarget(range, 2), RandomDistribution.uniform());
			}).isInstanceOf(IllegalArgumentException.class);
		}
	}

	private void assertAllPartitionsAreCovered(
		RandomGenerator<BigInteger> generator, BigInteger min, BigInteger max,
		List<BigInteger> partitionPoints
	) {
		Collections.sort(partitionPoints);
		BigInteger lower = min;
		for (BigInteger partitionPoint : partitionPoints) {
			BigInteger l = lower;
			assertAtLeastOneGenerated(
				generator,
				integral -> integral.compareTo(l) >= 0 && integral.compareTo(partitionPoint) < 0,
				String.format("No value created between %s and %s", l, partitionPoint)
			);
			lower = partitionPoint;
		}
		BigInteger l = lower;
		assertAtLeastOneGenerated(
			generator,
			integral -> integral.compareTo(l) >= 0 && integral.compareTo(max) < 0,
			String.format("No value created between %s and %s", l, max)
		);
	}

	private void assertAllWithinRange(RandomGenerator<BigInteger> generator, BigInteger min, BigInteger max) {
		ArbitraryTestHelper.assertAllGenerated(
			generator,
			integral -> integral.compareTo(min) >= 0 && integral.compareTo(max) <= 0
		);
	}

}
