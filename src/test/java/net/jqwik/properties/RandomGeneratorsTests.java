package net.jqwik.properties;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Set;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.RandomGenerators;

class RandomGeneratorsTests {

	// TODO: Move generator tests from ArbitrariesTests here

	@Example
	void setsAreGeneratedWithCorrectMinAndMaxSize() {
		RandomGenerator<Integer> integerGenerator = RandomGenerators.choose(1, 10);
		RandomGenerator<Set<Integer>> generator = RandomGenerators.set(integerGenerator, 2, 5);
		ArbitraryTestHelper.assertAllGenerated(generator, set -> set.size() >= 2 && set.size() <= 5);
	}

	@Group
	class DecimalGeneration {

		@Example
		void smallBigDecimals() {
			BigDecimal min = new BigDecimal(-10);
			BigDecimal max = new BigDecimal(10);
			RandomGenerator<BigDecimal> generator = RandomGenerators.decimals(min, max, 2);
			ArbitraryTestHelper.assertAllGenerated(generator, //
					decimal -> decimal.compareTo(min) >= 0 && decimal.compareTo(max) <= 0 && decimal.scale() == 2);
		}

		@Example
		void bigBigDecimals() {
			BigDecimal min = new BigDecimal(-Double.MAX_VALUE);
			BigDecimal max = new BigDecimal(Double.MAX_VALUE);
			RandomGenerator<BigDecimal> generator = RandomGenerators.decimals(min, max, 0);
			ArbitraryTestHelper.assertAllGenerated(generator, decimal -> {
				System.out.println(decimal.doubleValue());
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
	}
}
