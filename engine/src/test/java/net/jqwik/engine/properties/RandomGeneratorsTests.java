package net.jqwik.engine.properties;

import java.math.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

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

	@Example
	void samplesAreGeneratedFirst(@ForAll Random random) {
		RandomGenerator<Integer> generator = RandomGenerators.integers(1, 10).withSamples(-1, -2);

		Assertions.assertThat(generator.next(random).value()).isEqualTo(-1);
		Assertions.assertThat(generator.next(random).value()).isEqualTo(-2);

		ArbitraryTestHelper.assertAllGenerated(generator, anInt -> anInt >= 1 && anInt <= 10);
	}

	@Group
	class IntegralGeneration {

		@Example
		void withinIntegerRange() {
			BigInteger min = valueOf(Integer.MIN_VALUE);
			BigInteger max = valueOf(Integer.MAX_VALUE);
			RandomGenerator<BigInteger> generator =
				RandomGenerators.bigIntegers(min, max, RandomGenerators.defaultShrinkingTargetCalculator(min, max));
			assertAllWithinRange(generator, min, max);
		}

		@Example
		void withinSmallRange() {
			BigInteger min = valueOf(-100);
			BigInteger max = valueOf(10000);
			RandomGenerator<BigInteger> generator =
				RandomGenerators.bigIntegers(min, max, RandomGenerators.defaultShrinkingTargetCalculator(min, max));
			assertAllWithinRange(generator, min, max);
		}

		@Example
		void withinGreaterRange() {
			BigInteger min = valueOf(-100_000_000_000L);
			BigInteger max = valueOf(100_000_000_000L);
			RandomGenerator<BigInteger> generator =
				RandomGenerators.bigIntegers(min, max, RandomGenerators.defaultShrinkingTargetCalculator(min, max));
			assertAllWithinRange(generator, min, max);
		}

		@Example
		void smallRangeWithPartitions() {
			BigInteger min = valueOf(-100);
			BigInteger max = valueOf(100000);
			BigInteger[] partitionPoints = new BigInteger[]{BigInteger.ZERO, valueOf(100), valueOf(1000)};
			RandomGenerator<BigInteger> generator = RandomGenerators.bigIntegers(
				min,
				max,
				RandomGenerators.defaultShrinkingTargetCalculator(min, max), partitionPoints
			);

			assertAllWithinRange(generator, min, max);
			assertAllPartitionsAreCovered(generator, min, max, partitionPoints);
		}

		@Example
		void greaterRangeWithPartitions() {
			BigInteger min = valueOf(Long.MIN_VALUE);
			BigInteger max = valueOf(Long.MAX_VALUE);
			BigInteger[] partitionPoints = new BigInteger[]{BigInteger.ZERO, valueOf(-10000), valueOf(10000)};
			RandomGenerator<BigInteger> generator = RandomGenerators.bigIntegers(
				min,
				max,
				RandomGenerators.defaultShrinkingTargetCalculator(min, max), partitionPoints
			);

			assertAllWithinRange(generator, min, max);
			assertAllPartitionsAreCovered(generator, min, max, partitionPoints);
		}

		@Example
		void outsideLongRange() {
			BigInteger min = new BigInteger("-10000000000000000000");
			BigInteger max = new BigInteger("10000000000000000000");
			RandomGenerator<BigInteger> generator =
				RandomGenerators.bigIntegers(min, max, RandomGenerators.defaultShrinkingTargetCalculator(min, max));
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
				RandomGenerators.bigIntegers(min, max, RandomGenerators.defaultShrinkingTargetCalculator(min, max));
			}).isInstanceOf(JqwikException.class);
		}

	}

	@Group
	class DecimalGeneration {

		@Example
		void smallBigDecimals() {
			BigDecimal min = new BigDecimal(-10);
			BigDecimal max = new BigDecimal(10);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators.bigDecimals(
					min, max, 2,
					RandomGenerators.defaultShrinkingTargetCalculator(min, max)
				);
			ArbitraryTestHelper.assertAllGenerated(
				generator, //
				decimal -> decimal.compareTo(min) >= 0 && decimal.compareTo(max) <= 0 && decimal.scale() == 2
			);
		}

		@Example
		void bigBigDecimals() {
			BigDecimal min = new BigDecimal(-Double.MAX_VALUE);
			BigDecimal max = new BigDecimal(Double.MAX_VALUE);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators.bigDecimals(
					min, max, 0,
					RandomGenerators.defaultShrinkingTargetCalculator(min, max)
				);
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
			RandomGenerator<BigInteger> generator =
				RandomGenerators.bigDecimals(
					min, max, 0,
					RandomGenerators.defaultShrinkingTargetCalculator(min, max),
					partitionPoints
				).map(BigDecimal::toBigInteger);

			assertAllWithinRange(generator, min.toBigInteger(), max.toBigInteger());
			assertAllPartitionsAreCovered(generator, min.toBigInteger(), max.toBigInteger(), //
										  new BigInteger[]{BigInteger.ZERO, valueOf(100), valueOf(1000)} //
			);
		}

		@Example
		void greaterRangeWithPartitions() {
			BigDecimal min = BigDecimal.valueOf(Long.MIN_VALUE);
			BigDecimal max = BigDecimal.valueOf(Long.MAX_VALUE);
			BigDecimal[] partitionPoints = new BigDecimal[]{BigDecimal.ZERO, BigDecimal.valueOf(-10000), BigDecimal.valueOf(10000)};
			RandomGenerator<BigInteger> generator =
				RandomGenerators.bigDecimals(
					min, max, 0,
					RandomGenerators.defaultShrinkingTargetCalculator(min, max),
					partitionPoints
				).map(BigDecimal::toBigInteger);

			assertAllWithinRange(generator, min.toBigInteger(), max.toBigInteger());
			assertAllPartitionsAreCovered(generator, min.toBigInteger(), max.toBigInteger(), //
										  new BigInteger[]{BigInteger.ZERO, valueOf(-10000), valueOf(10000)} //
			);
		}

		@Example
		void minGreaterThanMaxFails() {
			assertThatThrownBy(() -> {
				BigDecimal min = BigDecimal.valueOf(1);
				BigDecimal max = BigDecimal.valueOf(-1);
				RandomGenerators.bigDecimals(min, max, 2, RandomGenerators.defaultShrinkingTargetCalculator(min, max));
			}).isInstanceOf(JqwikException.class);
		}
	}

	@Group
	class EdgeCases {

		@Example
		void withEdgeCasesInjectsEdgeCasesIntoGeneratedValues() {
			Shrinkable<Integer> zero = createShrinkableInt(0);
			Shrinkable<Integer> thousand = createShrinkableInt(1000);
			List<Shrinkable<Integer>> edgeCases = Arrays.asList(zero, thousand);
			RandomGenerator<Integer> generator = RandomGenerators.integers(0, 1000).withEdgeCases(100, edgeCases);

			assertAtLeastOneGenerated(generator, i -> i.equals(0));
			assertAtLeastOneGenerated(generator, i -> i.equals(1000));
		}

		@Example
		void edgeCaseIsAlsoShrunkToNonEdgeCase(@ForAll Random random) {
			Shrinkable<Integer> zero = createShrinkableInt(0);
			Shrinkable<Integer> thousand = createShrinkableInt(1000);
			List<Shrinkable<Integer>> edgeCases = Arrays.asList(zero, thousand);
			RandomGenerator<Integer> generator = RandomGenerators.integers(0, 1000).withEdgeCases(10, edgeCases);

			Shrinkable<Integer> thousandGenerated = generateUntil(generator, random, i -> i.equals(1000));

			assertThat(thousandGenerated.value()).isEqualTo(1000);
			ShrinkingSequence<Integer> sequence = thousandGenerated.shrink(i -> i < 5);
			while (sequence.next(() -> {}, ignore -> {})) { }
			assertThat(sequence.current().value()).isEqualTo(5);
		}

		private Shrinkable<Integer> createShrinkableInt(int value) {
			return new ShrinkableBigInteger(
				valueOf(value),
				Range.of(valueOf(0), valueOf(1000)),
				BigInteger.ZERO
			).map(BigInteger::intValueExact);
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
			assertAtLeastOneGenerated(
				generator, //
				integral -> integral.compareTo(l) >= 0 && integral.compareTo(partitionPoint) < 0,
				String.format("No value created between %s and %s", l, partitionPoint)
			);
			lower = partitionPoint;
		}
		BigInteger l = lower;
		assertAtLeastOneGenerated(
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
