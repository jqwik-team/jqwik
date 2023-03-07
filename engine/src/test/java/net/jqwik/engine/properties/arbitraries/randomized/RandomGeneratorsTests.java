package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

import static java.math.BigInteger.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

class RandomGeneratorsTests {

	@Example
	void setsAreGeneratedWithCorrectMinAndMaxSize(@ForAll Random random) {
		RandomGenerator<Integer> integerGenerator = RandomGenerators.integers(1, 10);
		RandomGenerator<Set<Integer>> generator = RandomGenerators.set(integerGenerator, 2, 5, 1000, null);
		checkAllGenerated(generator, random, set -> set.size() >= 2 && set.size() <= 5);
	}

	@Example
	void setGenerationShouldStopWithTooManyMisses(@ForAll Random random) {
		RandomGenerator<Integer> integerGenerator = RandomGenerators.integers(1, 10);
		RandomGenerator<Set<Integer>> generator = RandomGenerators.set(integerGenerator, 11, 11, 1000, null);

		Assertions.assertThatThrownBy(() -> generator.next(random))
				  .isInstanceOf(JqwikException.class);
	}

	@Group
	class IntegralGeneration {

		@Example
		void withinIntegerRange(@ForAll Random random) {
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
			assertAllWithinRange(generator, random, min, max);
		}

		@Example
		void withinSmallRange(@ForAll Random random) {
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
			assertAllWithinRange(generator, random, min, max);
		}

		@Example
		void withinGreaterRange(@ForAll Random random) {
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
			assertAllWithinRange(generator, random, min, max);
		}

		@Example
		void smallRangeWithBiasedDistribution(@ForAll Random random) {
			BigInteger min = valueOf(-100);
			BigInteger max = valueOf(100000);
			RandomGenerator<BigInteger> generator = RandomGenerators.bigIntegers(
				min,
				max,
				RandomIntegralGenerators.defaultShrinkingTarget(Range.of(min, max)),
				RandomDistribution.biased()
			);

			assertAllWithinRange(generator, random, min, max);
		}

		@Example
		void greaterRangeWithPartitions(@ForAll Random random) {
			BigInteger min = valueOf(Long.MIN_VALUE);
			BigInteger max = valueOf(Long.MAX_VALUE);
			RandomGenerator<BigInteger> generator = RandomGenerators.bigIntegers(
				min,
				max,
				RandomIntegralGenerators.defaultShrinkingTarget(Range.of(min, max)),
				RandomDistribution.biased()
			);
			assertAllWithinRange(generator, random, min, max);
		}

		@Example
		void rangeWithGaussianDistribution(@ForAll Random random) {
			BigInteger min = valueOf(-1000);
			BigInteger max = valueOf(1000);
			RandomGenerator<BigInteger> generator = RandomGenerators.bigIntegers(
				min,
				max,
				RandomIntegralGenerators.defaultShrinkingTarget(Range.of(min, max)),
				RandomDistribution.gaussian()
			);

			assertAllWithinRange(generator, random, min, max);
		}

		@Example
		void outsideLongRange(@ForAll Random random) {
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
			assertAllWithinRange(generator, random, min, max);
			checkAtLeastOneGenerated(
				generator,
				random,
				bigInteger -> bigInteger.compareTo(valueOf(Long.MAX_VALUE)) > 0
			);
			checkAtLeastOneGenerated(
				generator,
				random,
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
		void smalls(@ForAll Random random) {
			BigDecimal min = new BigDecimal(-10);
			BigDecimal max = new BigDecimal(10);
			Range<BigDecimal> range = Range.of(min, max);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators.bigDecimals(
					range, 2,
					RandomDecimalGenerators.defaultShrinkingTarget(range, 2),
					RandomDistribution.uniform()
				);
			checkAllGenerated(
				generator,
				random,
				decimal -> decimal.compareTo(min) >= 0
							   && decimal.compareTo(max) <= 0
							   && decimal.scale() == 2
			);
		}

		@Example
		void bordersExcluded(@ForAll Random random) {
			BigDecimal min = new BigDecimal(-10);
			BigDecimal max = new BigDecimal(10);
			Range<BigDecimal> range = Range.of(min, false, max, false);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators.bigDecimals(
					range, 1,
					RandomDecimalGenerators.defaultShrinkingTarget(range, 1),
					RandomDistribution.uniform()
				);
			checkAllGenerated(
				generator,
				random,
				decimal -> decimal.compareTo(min) > 0
							   && decimal.compareTo(max) < 0
							   && decimal.scale() == 1
			);
		}

		@Example
		void bordersExcludedAllPositive(@ForAll Random random) {
			BigDecimal min = new BigDecimal(1);
			BigDecimal max = new BigDecimal(10);
			Range<BigDecimal> range = Range.of(min, false, max, false);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators.bigDecimals(
					range, 1,
					RandomDecimalGenerators.defaultShrinkingTarget(range, 1),
					RandomDistribution.uniform()
				);
			checkAllGenerated(
				generator,
				random,
				decimal -> decimal.compareTo(min) > 0
							   && decimal.compareTo(max) < 0
							   && decimal.scale() == 1
			);
		}

		@Example
		void bordersExcludedAllNegative(@ForAll Random random) {
			BigDecimal min = new BigDecimal(-10);
			BigDecimal max = new BigDecimal(-1);
			Range<BigDecimal> range = Range.of(min, false, max, false);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators.bigDecimals(
					range, 1,
					RandomDecimalGenerators.defaultShrinkingTarget(range, 1),
					RandomDistribution.uniform()
				);
			checkAllGenerated(
				generator,
				random,
				decimal -> decimal.compareTo(min) > 0
							   && decimal.compareTo(max) < 0
							   && decimal.scale() == 1
			);
		}

		@Example
		void smallRange(@ForAll Random random) {
			BigDecimal min = new BigDecimal("0.01");
			BigDecimal max = new BigDecimal("0.03");
			Range<BigDecimal> range = Range.of(min, false, max, false);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators.bigDecimals(
					range, 2,
					RandomDecimalGenerators.defaultShrinkingTarget(range, 2),
					RandomDistribution.uniform()
				);
			checkAllGenerated(
				generator,
				random,
				decimal -> {return decimal.equals(new BigDecimal("0.02"));}
			);
		}

		@Example
		void impossibleRange() {
			BigDecimal min = new BigDecimal(0);
			BigDecimal max = new BigDecimal(1);
			Range<BigDecimal> range = Range.of(min, false, max, false);
			assertThatThrownBy(
				() -> RandomGenerators.bigDecimals(
					range, 0,
					RandomDecimalGenerators.defaultShrinkingTarget(range, 0),
					RandomDistribution.uniform()
				)
			).isInstanceOf(JqwikException.class);
		}

		@Example
		void bigBigDecimals(@ForAll Random random) {
			BigDecimal min = BigDecimal.valueOf(-Double.MAX_VALUE);
			BigDecimal max = BigDecimal.valueOf(Double.MAX_VALUE);
			Range<BigDecimal> range = Range.of(min, max);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators.bigDecimals(
					range, 0,
					RandomDecimalGenerators.defaultShrinkingTarget(range, 0),
					RandomDistribution.uniform()
				);
			assertAllGenerated(
				generator,
				random,
				decimal -> {
					assertThat(decimal).isBetween(min, max);
					assertThat(decimal.scale()).isEqualTo(0);
				}
			);
		}

		@Example
		void smallRangeWithBiasedDistribution(@ForAll Random random) {
			Range<BigDecimal> range = Range.of(BigDecimal.valueOf(-100), BigDecimal.valueOf(100000));
			RandomGenerator<BigInteger> generator =
				RandomGenerators.bigDecimals(
					range, 0,
					RandomDecimalGenerators.defaultShrinkingTarget(range, 0),
					RandomDistribution.biased()
				).map(BigDecimal::toBigInteger);

			assertAllWithinRange(
				generator,
				random,
				BigDecimal.valueOf(-100).toBigInteger(), BigDecimal.valueOf(100000).toBigInteger()
			);
			assertAllPartitionsAreCovered(
				generator,
				random,
				BigDecimal.valueOf(-100).toBigInteger(), BigDecimal.valueOf(100000).toBigInteger(),
				Arrays.asList(BigInteger.ZERO, valueOf(100), valueOf(1000))
			);
		}

		@Example
		void greaterRangeWithBiasedDistribution(@ForAll Random random) {
			Range<BigDecimal> range = Range.of(BigDecimal.valueOf(Long.MIN_VALUE), BigDecimal.valueOf(Long.MAX_VALUE));
			RandomGenerator<BigInteger> generator =
				RandomGenerators.bigDecimals(
					range, 0,
					RandomDecimalGenerators.defaultShrinkingTarget(range, 0),
					RandomDistribution.biased()
				).map(BigDecimal::toBigInteger);

			assertAllWithinRange(
				generator,
				random,
				BigDecimal.valueOf(Long.MIN_VALUE).toBigInteger(),
				BigDecimal.valueOf(Long.MAX_VALUE).toBigInteger()
			);
			assertAllPartitionsAreCovered(
				generator,
				random,
				BigDecimal.valueOf(Long.MIN_VALUE).toBigInteger(),
				BigDecimal.valueOf(Long.MAX_VALUE).toBigInteger(),
				Arrays.asList(BigInteger.ZERO, valueOf(-10000), valueOf(10000))
			);
		}

		@Example
		void minGreaterThanMaxFails() {
			assertThatThrownBy(() -> {
				Range<BigDecimal> range = Range.of(BigDecimal.valueOf(1), BigDecimal.valueOf(-1));
				RandomGenerators.bigDecimals(
					range, 2,
					RandomDecimalGenerators.defaultShrinkingTarget(range, 2),
					RandomDistribution.uniform()
				);
			}).isInstanceOf(IllegalArgumentException.class);
		}
	}

	private void assertAllPartitionsAreCovered(
		RandomGenerator<BigInteger> generator,
		Random random,
		BigInteger min, BigInteger max,
		List<BigInteger> partitionPoints
	) {
		Collections.sort(partitionPoints);
		BigInteger lower = min;
		for (BigInteger partitionPoint : partitionPoints) {
			BigInteger l = lower;
			checkAtLeastOneGenerated(
				generator,
				random,
				integral -> integral.compareTo(l) >= 0 && integral.compareTo(partitionPoint) < 0,
				String.format("No value created between %s and %s", l, partitionPoint)
			);
			lower = partitionPoint;
		}
		BigInteger l = lower;
		checkAtLeastOneGenerated(
			generator,
			random,
			integral -> integral.compareTo(l) >= 0 && integral.compareTo(max) < 0,
			String.format("No value created between %s and %s", l, max)
		);
	}

	private void assertAllWithinRange(RandomGenerator<BigInteger> generator, Random random, BigInteger min, BigInteger max) {
		checkAllGenerated(
			generator,
			random,
			integral -> integral.compareTo(min) >= 0 && integral.compareTo(max) <= 0
		);
	}

}
