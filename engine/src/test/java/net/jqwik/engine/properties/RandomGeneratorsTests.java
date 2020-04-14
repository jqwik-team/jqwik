package net.jqwik.engine.properties;

import java.math.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
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
				RandomGenerators.bigDecimals(
					range, 2,
					RandomGenerators.defaultShrinkingTargetCalculator(range, 2)
				);
			ArbitraryTestHelper.assertAllGenerated(
				generator, //
				decimal -> decimal.compareTo(min) >= 0 && decimal.compareTo(max) <= 0 && decimal.scale() == 2
			);
		}

		@Example
		void bordersExcluded() {
			BigDecimal min = new BigDecimal(-10);
			BigDecimal max = new BigDecimal(10);
			Range<BigDecimal> range = Range.of(min, false, max, false);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators.bigDecimals(
					range, 1,
					RandomGenerators.defaultShrinkingTargetCalculator(range, 1)
				);
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
				RandomGenerators.bigDecimals(
					range, 1,
					RandomGenerators.defaultShrinkingTargetCalculator(range, 1)
				);
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
				RandomGenerators.bigDecimals(
					range, 1,
					RandomGenerators.defaultShrinkingTargetCalculator(range, 1)
				);
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
				RandomGenerators.bigDecimals(
					range, 2,
					RandomGenerators.defaultShrinkingTargetCalculator(range, 2)
				);
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
				() -> RandomGenerators.bigDecimals(
					range, 0,
					RandomGenerators.defaultShrinkingTargetCalculator(range, 0)
				)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void bigBigDecimals() {
			BigDecimal min = BigDecimal.valueOf(-Double.MAX_VALUE);
			BigDecimal max = BigDecimal.valueOf(Double.MAX_VALUE);
			Range<BigDecimal> range = Range.of(min, max);
			RandomGenerator<BigDecimal> generator =
				RandomGenerators.bigDecimals(
					range, 0,
					RandomGenerators.defaultShrinkingTargetCalculator(range, 0)
				);
			ArbitraryTestHelper.assertAllGenerated(generator, decimal -> {
				assertThat(decimal).isBetween(min, max);
				assertThat(decimal.scale()).isEqualTo(0);
			});
		}

		@Example
		void smallRangeWithPartitions() {
			Range<BigDecimal> range = Range.of(BigDecimal.valueOf(-100), BigDecimal.valueOf(100000));
			BigDecimal[] partitionPoints = new BigDecimal[]{BigDecimal.ZERO, BigDecimal.valueOf(100), BigDecimal.valueOf(1000)};
			RandomGenerator<BigInteger> generator =
				RandomGenerators.bigDecimals(
					range, 0,
					RandomGenerators.defaultShrinkingTargetCalculator(range, 0),
					partitionPoints
				).map(BigDecimal::toBigInteger);

			assertAllWithinRange(generator, BigDecimal.valueOf(-100).toBigInteger(), BigDecimal.valueOf(100000).toBigInteger());
			assertAllPartitionsAreCovered(generator, BigDecimal.valueOf(-100).toBigInteger(), BigDecimal.valueOf(100000).toBigInteger(), //
										  new BigInteger[]{BigInteger.ZERO, valueOf(100), valueOf(1000)} //
			);
		}

		@Example
		void greaterRangeWithPartitions() {
			Range<BigDecimal> range = Range.of(BigDecimal.valueOf(Long.MIN_VALUE), BigDecimal.valueOf(Long.MAX_VALUE));
			BigDecimal[] partitionPoints = new BigDecimal[]{BigDecimal.ZERO, BigDecimal.valueOf(-10000), BigDecimal.valueOf(10000)};
			RandomGenerator<BigInteger> generator =
				RandomGenerators.bigDecimals(
					range, 0,
					RandomGenerators.defaultShrinkingTargetCalculator(range, 0),
					partitionPoints
				).map(BigDecimal::toBigInteger);

			assertAllWithinRange(generator, BigDecimal.valueOf(Long.MIN_VALUE).toBigInteger(), BigDecimal.valueOf(Long.MAX_VALUE)
																										 .toBigInteger());
			assertAllPartitionsAreCovered(generator, BigDecimal.valueOf(Long.MIN_VALUE).toBigInteger(), BigDecimal.valueOf(Long.MAX_VALUE)
																												  .toBigInteger(), //
										  new BigInteger[]{BigInteger.ZERO, valueOf(-10000), valueOf(10000)} //
			);
		}

		@Example
		void minGreaterThanMaxFails() {
			assertThatThrownBy(() -> {
				Range<BigDecimal> range = Range.of(BigDecimal.valueOf(1), BigDecimal.valueOf(-1));
				RandomGenerators.bigDecimals(range, 2, RandomGenerators.defaultShrinkingTargetCalculator(range, 2));
			}).isInstanceOf(IllegalArgumentException.class);
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
			ShrinkingSequence<Integer> sequence = thousandGenerated.shrink((TestingFalsifier<Integer>) i -> i < 5);
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
