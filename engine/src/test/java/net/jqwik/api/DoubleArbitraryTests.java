package net.jqwik.api;

import java.util.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.edgeCases.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
class DoubleArbitraryTests {

	@Example
	void doubleMinsAndMaxesWithEdgeCases(@ForAll Random random) {
		RandomGenerator<Double> generator = Arbitraries.doubles().generator(1, true);
		TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value == 0.01);
		TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value == -0.01);
		TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value == -Double.MAX_VALUE);
		TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value == Double.MAX_VALUE);
	}

	@Example
	void doubles(@ForAll Random random) {
		Arbitrary<Double> doubleArbitrary = Arbitraries.doubles().between(-10.0, 10.0).ofScale(2);
		RandomGenerator<Double> generator = doubleArbitrary.generator(1);

		TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value == 0.0);
		TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value < -1.0 && value > -9.0);
		TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value > 1.0 && value < 9.0);
		checkAllGenerated(generator, random, value -> {
			double rounded = Math.round(value * 100) / 100.0;
			return value >= -10.0 && value <= 10.0 && value == rounded;
		});
	}

	@Example
	void doublesWithMaximumRange(@ForAll Random random) {
		double min = -Double.MAX_VALUE;
		Arbitrary<Double> doubleArbitrary = Arbitraries.doubles().between(min, Double.MAX_VALUE).ofScale(2);
		RandomGenerator<Double> generator = doubleArbitrary.generator(100, true);

		TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value == 0.0);
		TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value < -1000.0);
		TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value > 1000.0);
	}

	@Example
	void doublesBorderIsMorePreciseThanScale() {
		double min = 0.001;
		double max = 0.199;
		Arbitrary<Double> arbitrary = Arbitraries.doubles().between(min, max).ofScale(2);
		assertThatThrownBy(() -> arbitrary.generator(1)).isInstanceOf(JqwikException.class);
	}

	@Example
	void excludedBordersDontAllowValueCreation() {
		double min = 0.01;
		double max = 0.02;
		Arbitrary<Double> arbitrary = Arbitraries.doubles().between(min, false, max, false).ofScale(2);
		assertThatThrownBy(() -> arbitrary.generator(1)).isInstanceOf(JqwikException.class);
	}

	@Example
	void doublesWithBordersExcluded(@ForAll Random random) {
		double min = 1.0;
		double max = 2.0;
		Arbitrary<Double> doubleArbitrary = Arbitraries.doubles().between(min, false, max, false).ofScale(1);
		RandomGenerator<Double> generator = doubleArbitrary.generator(100);
		checkAllGenerated(generator, random, value -> value > min && value < max);
	}

	@Example
	void doublesLessThan(@ForAll Random random) {
		double max = 2.0;
		Arbitrary<Double> doubleArbitrary = Arbitraries.doubles().lessThan(max).ofScale(0);
		RandomGenerator<Double> generator = doubleArbitrary.generator(100);
		checkAllGenerated(generator, random, value -> value < max);
	}

	@Example
	void doublesLessOrEqual(@ForAll Random random) {
		double max = 2.0;
		Arbitrary<Double> doubleArbitrary = Arbitraries.doubles().lessOrEqual(max).ofScale(0);
		RandomGenerator<Double> generator = doubleArbitrary.generator(100);
		checkAllGenerated(generator, random, value -> value <= max);
	}

	@Example
	void doublesGreaterThan(@ForAll Random random) {
		double min = 2.0;
		Arbitrary<Double> doubleArbitrary = Arbitraries.doubles().greaterThan(min).ofScale(0);
		RandomGenerator<Double> generator = doubleArbitrary.generator(100);
		checkAllGenerated(generator, random, value -> value > min);
	}

	@Example
	void doublesGreaterOrEqual(@ForAll Random random) {
		double min = 2.0;
		Arbitrary<Double> doubleArbitrary = Arbitraries.doubles().greaterOrEqual(min).ofScale(0);
		RandomGenerator<Double> generator = doubleArbitrary.generator(100);
		checkAllGenerated(generator, random, value -> value >= min);
	}

	@Example
	void doublesWithShrinkingTargetOutsideBorders() {
		Arbitrary<Double> arbitrary = Arbitraries.doubles()
												 .between(1.0, 10.0)
												 .shrinkTowards(-1.0);
		assertThatThrownBy(() -> arbitrary.generator(1)).isInstanceOf(JqwikException.class);
	}

	@Example
	void doublesWithSpecials(@ForAll Random random) {
		Arbitrary<Double> arbitrary = Arbitraries.doubles().between(1.0, 10.0)
												 .withSpecialValue(Double.NaN)
												 .withSpecialValue(Double.MIN_VALUE);
		RandomGenerator<Double> generator = arbitrary.generator(100);

		checkAllGenerated(
			generator, random,
			value -> (value >= 1.0 && value <= 10.0) || value.equals(Double.NaN) || value.equals(Double.MIN_VALUE)
		);

		TestingSupport.checkAtLeastOneGenerated(
			generator, random, value -> value >= 1.0 && value <= 10.0
		);

		assertAtLeastOneGeneratedOf(
			generator, random, Double.NaN, Double.MIN_VALUE
		);
	}

	@Example
	void doublesWithStandardSpecials(@ForAll Random random) {
		Arbitrary<Double> arbitrary = Arbitraries.doubles().between(1.0, 10.0)
												 .withStandardSpecialValues();
		RandomGenerator<Double> generator = arbitrary.generator(100);

		assertAtLeastOneGeneratedOf(
			generator, random,
			Double.NaN,
			Double.MIN_VALUE, Double.MIN_NORMAL,
			Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY
		);
	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void singleDouble() {
			Optional<ExhaustiveGenerator<Double>> optionalGenerator =
				Arbitraries.doubles()
						   .between(100.0, 100.0)
						   .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Double> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(1);
			assertThat(generator).containsExactly(100.0);
		}

		@Example
		void doublesWithSpecials() {
			Optional<ExhaustiveGenerator<Double>> optionalGenerator =
				Arbitraries.doubles()
						   .between(100.0, 100.0)
						   .withSpecialValue(Double.MIN_NORMAL)
						   .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Double> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(2);
			assertThat(generator).containsExactlyInAnyOrder(100.0, Double.MIN_NORMAL);
		}

		@Example
		void doubleRangeDoesNotAllowExhaustiveGeneration() {
			Optional<ExhaustiveGenerator<Double>> optionalGenerator =
				Arbitraries.doubles()
						   .between(1.0, 100.0)
						   .exhaustive();
			assertThat(optionalGenerator).isEmpty();
		}
	}

	@Group
	class GenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.of(
				Arbitraries.doubles(),
				Arbitraries.doubles().withStandardSpecialValues()
			);
		}
	}

	@Group
	class EdgeCasesGeneration implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.of(
				Arbitraries.doubles(),
				Arbitraries.doubles().withStandardSpecialValues()
			);
		}

		@Example
		void doubles() {
			int scale = 2;
			DoubleArbitrary arbitrary = Arbitraries.doubles()
												   .between(-10.0, 10.0)
												   .ofScale(scale);
			EdgeCases<Double> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				-10.0, -1.0, -0.01, 0.0, 0.01, 1.0, 10.0
			);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(7);
		}

		@Example
		void doublesWithSpecials() {
			int scale = 1;
			DoubleArbitrary arbitrary = Arbitraries.doubles()
												   .between(1.0, 10.0)
												   .ofScale(scale)
												   .withSpecialValue(Double.NaN)
												   .withSpecialValue(Double.NEGATIVE_INFINITY);
			EdgeCases<Double> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				1.0, 10.0, Double.NaN, Double.NEGATIVE_INFINITY
			);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(4);
		}

	}
}
