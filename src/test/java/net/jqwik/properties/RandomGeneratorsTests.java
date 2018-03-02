package net.jqwik.properties;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;

import java.math.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class RandomGeneratorsTests {

	// TODO: Move generator tests from ArbitrariesTests here

	@Example
	void setsAreGeneratedWithCorrectMinAndMaxSize() {
		RandomGenerator<Integer> integerGenerator = RandomGenerators.integers(1, 10);
		RandomGenerator<Set<Integer>> generator = RandomGenerators.set(integerGenerator, 2, 5);
		ArbitraryTestHelper.assertAllGenerated(generator, set -> set.size() >= 2 && set.size() <= 5);
	}

	@Group
	class IntegralGeneration {

		@Example
		void withinIntegerRange() {
			BigInteger min = BigInteger.valueOf(Integer.MIN_VALUE);
			BigInteger max = BigInteger.valueOf(Integer.MAX_VALUE);
			RandomGenerator<BigInteger> generator = RandomGenerators.bigIntegers(min, max);
			assertAllWithinRange(generator, min, max);
		}

		@Example
		void withinSmallRange() {
			BigInteger min = BigInteger.valueOf(-100);
			BigInteger max = BigInteger.valueOf(10000);
			RandomGenerator<BigInteger> generator = RandomGenerators.bigIntegers(min, max);
			assertAllWithinRange(generator, min, max);
		}

		@Example
		void withinGreaterRange() {
			BigInteger min = BigInteger.valueOf(-100_000_000_000L);
			BigInteger max = BigInteger.valueOf(100_000_000_000L);
			RandomGenerator<BigInteger> generator = RandomGenerators.bigIntegers(min, max);
			assertAllWithinRange(generator, min, max);
		}

		@Example
		void smallRangeWithPartitions() {
			BigInteger min = BigInteger.valueOf(-100);
			BigInteger max = BigInteger.valueOf(100000);
			BigInteger[] partitionPoints = new BigInteger[]{BigInteger.ZERO, BigInteger.valueOf(100), BigInteger.valueOf(1000)};
			RandomGenerator<BigInteger> generator = RandomGenerators.bigIntegers(min, max, partitionPoints);

			assertAllWithinRange(generator, min, max);
			assertAllPartitionsAreCovered(generator, min, max, partitionPoints);
		}

		@Example
		void greaterRangeWithPartitions() {
			BigInteger min = BigInteger.valueOf(Long.MIN_VALUE);
			BigInteger max = BigInteger.valueOf(Long.MAX_VALUE);
			BigInteger[] partitionPoints = new BigInteger[]{BigInteger.ZERO, BigInteger.valueOf(-10000), BigInteger.valueOf(10000)};
			RandomGenerator<BigInteger> generator = RandomGenerators.bigIntegers(min, max, partitionPoints);

			assertAllWithinRange(generator, min, max);
			assertAllPartitionsAreCovered(generator, min, max, partitionPoints);
		}

		@Example
		void minGreaterThanMaxFails() {
			assertThatThrownBy(() -> RandomGenerators.bigIntegers(BigInteger.valueOf(1), BigInteger.valueOf(-1)))
				.isInstanceOf(JqwikException.class);
		}

	}
	
	@Group
	class DecimalGeneration {

		@Example
		void smallBigDecimals() {
			BigDecimal min = new BigDecimal(-10);
			BigDecimal max = new BigDecimal(10);
			RandomGenerator<BigDecimal> generator = RandomGenerators.bigDecimals(min, max, 2);
			ArbitraryTestHelper.assertAllGenerated(generator, //
					decimal -> decimal.compareTo(min) >= 0 && decimal.compareTo(max) <= 0 && decimal.scale() == 2);
		}

		@Example
		void bigBigDecimals() {
			BigDecimal min = new BigDecimal(-Double.MAX_VALUE);
			BigDecimal max = new BigDecimal(Double.MAX_VALUE);
			RandomGenerator<BigDecimal> generator = RandomGenerators.bigDecimals(min, max, 0);
			ArbitraryTestHelper.assertAllGenerated(generator, decimal -> {
				assertThat(decimal).isBetween(min, max);
				assertThat(decimal.scale()).isEqualTo(0);
			});
		}

		@Example
		void smallDoubles() {
			double min = -10;
			double max = 10;
			RandomGenerator<Double> generator = RandomGenerators.doubles(min, max, 2);
			ArbitraryTestHelper.assertAllGenerated(generator, decimal -> decimal >= min && decimal <= max);
		}

		@Example
		void bigDoubles() {
			double min = -Double.MAX_VALUE;
			double max = Double.MAX_VALUE;
			RandomGenerator<Double> generator = RandomGenerators.doubles(min, max, 2);
			ArbitraryTestHelper.assertAllGenerated(generator, decimal -> {
				assertThat(decimal).isBetween(min, max);
			});
		}

		@Example
		void smallFloats() {
			float min = -10;
			float max = 10;
			RandomGenerator<Float> generator = RandomGenerators.floats(min, max, 2);
			ArbitraryTestHelper.assertAllGenerated(generator, decimal -> decimal >= min && decimal <= max);
		}

		@Example
		void bigFloats() {
			float min = -Float.MAX_VALUE;
			float max = Float.MAX_VALUE;
			RandomGenerator<Float> generator = RandomGenerators.floats(min, max, 2);
			ArbitraryTestHelper.assertAllGenerated(generator, decimal -> {
				assertThat(decimal).isBetween(min, max);
			});
		}

		@Example
		void smallRangeWithPartitions() {
			BigDecimal min = BigDecimal.valueOf(-100);
			BigDecimal max = BigDecimal.valueOf(100000);
			BigDecimal[] partitionPoints = new BigDecimal[]{BigDecimal.ZERO, BigDecimal.valueOf(100), BigDecimal.valueOf(1000)};
			RandomGenerator<BigInteger> generator = RandomGenerators.bigDecimals(min, max, 0, partitionPoints)
																	.map(BigDecimal::toBigInteger);

			assertAllWithinRange(generator, min.toBigInteger(), max.toBigInteger());
			assertAllPartitionsAreCovered(generator, min.toBigInteger(), max.toBigInteger(), //
										  new BigInteger[]{BigInteger.ZERO, BigInteger.valueOf(100), BigInteger.valueOf(1000)} //
			);
		}

		@Example
		void greaterRangeWithPartitions() {
			BigDecimal min = BigDecimal.valueOf(Long.MIN_VALUE);
			BigDecimal max = BigDecimal.valueOf(Long.MAX_VALUE);
			BigDecimal[] partitionPoints = new BigDecimal[]{BigDecimal.ZERO, BigDecimal.valueOf(-10000), BigDecimal.valueOf(10000)};
			RandomGenerator<BigInteger> generator = RandomGenerators.bigDecimals(min, max, 0, partitionPoints)
																	.map(BigDecimal::toBigInteger);

			assertAllWithinRange(generator, min.toBigInteger(), max.toBigInteger());
			assertAllPartitionsAreCovered(generator, min.toBigInteger(), max.toBigInteger(), //
										  new BigInteger[]{BigInteger.ZERO, BigInteger.valueOf(-10000), BigInteger.valueOf(10000)} //
			);
		}

		@Example
		void minGreaterThanMaxFails() {
			assertThatThrownBy(() -> RandomGenerators.bigDecimals(BigDecimal.valueOf(1), BigDecimal.valueOf(-1), 2))
				.isInstanceOf(JqwikException.class);
		}



	}

	private void assertAllPartitionsAreCovered(
		RandomGenerator<BigInteger> generator, BigInteger min, BigInteger max,
		BigInteger[] partitionPoints
	) {
		Arrays.sort(partitionPoints);
		BigInteger lower = min;
		for (BigInteger partitionPoint : partitionPoints) {
			BigInteger l = lower;
			ArbitraryTestHelper.assertAtLeastOneGenerated(
				generator, //
				integral -> integral.compareTo(l) >= 0 && integral.compareTo(partitionPoint) < 0,
				String.format("No value created between %s and %s", l, partitionPoint)
			);
			lower = partitionPoint;
		}
		BigInteger l = lower;
		ArbitraryTestHelper.assertAtLeastOneGenerated(
			generator, //
			integral -> integral.compareTo(l) >= 0 && integral.compareTo(max) < 0,
			String.format("No value created between %s and %s", l, max)
		);
	}

	private void assertAllWithinRange(RandomGenerator<BigInteger> generator, BigInteger min, BigInteger max) {
		ArbitraryTestHelper.assertAllGenerated(
			generator, //
			integral -> integral.compareTo(min) >= 0 && integral.compareTo(max) <= 0
		);
	}


}
