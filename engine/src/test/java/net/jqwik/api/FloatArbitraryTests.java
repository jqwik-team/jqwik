package net.jqwik.api;

import java.util.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.edgeCases.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
class FloatArbitraryTests {

	@Example
	void floatMinsAndMaxesWithEdgeCases(@ForAll JqwikRandom random) {
		RandomGenerator<Float> generator = Arbitraries.floats().generator(1, true);
		assertAtLeastOneGeneratedOf(
			generator, random,
			0.01f, -0.01f, -Float.MAX_VALUE, Float.MAX_VALUE
		);
	}

	@Example
	void floats(@ForAll JqwikRandom random) {
		Arbitrary<Float> floatArbitrary = Arbitraries.floats().between(-10.0f, 10.0f).ofScale(2);
		RandomGenerator<Float> generator = floatArbitrary.generator(1, true);

		assertAtLeastOneGeneratedOf(generator, random, 0.0f);
		TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value < -1.0 && value > -9.0);
		TestingSupport.checkAtLeastOneGenerated(generator, random, value -> value > 1.0 && value < 9.0);
		checkAllGenerated(generator, random, value -> {
			float rounded = (float) (Math.round(value * 100) / 100.0);
			return value >= -10.0 && value <= 10.0 && value == rounded;
		});
	}

	@Example
	void floatsWithBordersExcluded(@ForAll JqwikRandom random) {
		float min = 1.0f;
		float max = 2.0f;
		Arbitrary<Float> floatArbitrary = Arbitraries.floats().between(min, false, max, false).ofScale(1);
		RandomGenerator<Float> generator = floatArbitrary.generator(100);
		checkAllGenerated(generator, random, value -> value > min && value < max);
	}

	@Example
	void floatsLessThan(@ForAll JqwikRandom random) {
		float max = 2.0f;
		Arbitrary<Float> floatArbitrary = Arbitraries.floats().lessThan(max).ofScale(0);
		RandomGenerator<Float> generator = floatArbitrary.generator(100);
		checkAllGenerated(generator, random, value -> value < max);
	}

	@Example
	void floatsLessOrEqual(@ForAll JqwikRandom random) {
		float max = 2.0f;
		Arbitrary<Float> floatArbitrary = Arbitraries.floats().lessOrEqual(max).ofScale(0);
		RandomGenerator<Float> generator = floatArbitrary.generator(100);
		checkAllGenerated(generator, random, value -> value <= max);
	}

	@Example
	void floatsGreaterThan(@ForAll JqwikRandom random) {
		float min = 2.0f;
		Arbitrary<Float> floatArbitrary = Arbitraries.floats().greaterThan(min).ofScale(0);
		RandomGenerator<Float> generator = floatArbitrary.generator(100);
		checkAllGenerated(generator, random, value -> value > min);
	}

	@Example
	void floatsGreaterOrEqual(@ForAll JqwikRandom random) {
		float min = 2.0f;
		Arbitrary<Float> floatArbitrary = Arbitraries.floats().greaterOrEqual(min).ofScale(0);
		RandomGenerator<Float> generator = floatArbitrary.generator(100);
		checkAllGenerated(generator, random, value -> value >= min);
	}

	@Example
	void floatsWithShrinkingTargetOutsideBorders() {
		Arbitrary<Float> arbitrary = Arbitraries.floats()
												.between(1.0f, 10.0f)
												.shrinkTowards(-1.0f);
		assertThatThrownBy(() -> arbitrary.generator(1)).isInstanceOf(JqwikException.class);
	}

	@Example
	void floatsWithSpecials(@ForAll JqwikRandom random) {
		Arbitrary<Float> arbitrary = Arbitraries.floats().between(1.0f, 10.0f)
												.withSpecialValue(Float.NaN)
												.withSpecialValue(Float.MIN_VALUE);
		RandomGenerator<Float> generator = arbitrary.generator(100);

		checkAllGenerated(
			generator, random,
			value -> (value >= 1.0f && value <= 10.0f) || value.equals(Float.NaN) || value.equals(Float.MIN_VALUE)
		);

		TestingSupport.checkAtLeastOneGenerated(
			generator, random, value -> value >= 1.0f && value <= 10.0f
		);

		TestingSupport.assertAtLeastOneGeneratedOf(
			generator, random, Float.NaN, Float.MIN_VALUE
		);
	}

	@Example
	void floatsWithStandardSpecials(@ForAll JqwikRandom random) {
		Arbitrary<Float> arbitrary = Arbitraries.floats().between(1.0f, 10.0f)
												.withStandardSpecialValues();
		RandomGenerator<Float> generator = arbitrary.generator(100);

		TestingSupport.assertAtLeastOneGeneratedOf(
			generator, random,
			Float.NaN,
			Float.MIN_VALUE, Float.MIN_NORMAL,
			Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY
		);
	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void singleFloat() {
			Optional<ExhaustiveGenerator<Float>> optionalGenerator =
				Arbitraries.floats()
						   .between(100.0f, 100.0f)
						   .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Float> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(1);
			assertThat(generator).containsExactly(100.0f);
		}

		@Example
		void floatWithSpecials() {
			Optional<ExhaustiveGenerator<Float>> optionalGenerator =
				Arbitraries.floats()
						   .between(100.0f, 100.0f)
						   .withSpecialValue(Float.MIN_NORMAL)
						   .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Float> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(2);
			assertThat(generator).containsExactlyInAnyOrder(100.0f, Float.MIN_NORMAL);
		}

		@Example
		void floatRangeDoesNotAllowExhaustiveGeneration() {
			Optional<ExhaustiveGenerator<Float>> optionalGenerator =
				Arbitraries.floats()
						   .between(1.0f, 100.0f)
						   .exhaustive();
			assertThat(optionalGenerator).isEmpty();
		}
	}

	@Group
	class GenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.of(
				Arbitraries.floats(),
				Arbitraries.floats().withStandardSpecialValues()
			);
		}
	}

	@Group
	class EdgeCasesGeneration implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.of(
				Arbitraries.floats(),
				Arbitraries.floats().withStandardSpecialValues()
			);
		}

		@Example
		void floats() {
			int scale = 2;
			FloatArbitrary arbitrary = Arbitraries.floats()
												  .between(-10.0f, 10.0f)
												  .ofScale(scale);
			EdgeCases<Float> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				-10.0f, -1.0f, -0.01f, 0.0f, 0.01f, 1.0f, 10.0f
			);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(7);
		}

		@Example
		void floatsWithSpecials() {
			int scale = 1;
			FloatArbitrary arbitrary = Arbitraries.floats()
												  .between(1.0f, 10.0f)
												  .ofScale(scale)
												  .withSpecialValue(Float.NaN)
												  .withSpecialValue(Float.NEGATIVE_INFINITY);
			EdgeCases<Float> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				1.0f, 10.0f, Float.NaN, Float.NEGATIVE_INFINITY
			);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(4);
		}

	}

}
