package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.*;

public class DefaultFloatArbitrary extends TypedCloneable implements FloatArbitrary {

	private static final float DEFAULT_MIN = -Float.MAX_VALUE;
	private static final float DEFAULT_MAX = Float.MAX_VALUE;

	private DecimalGeneratingArbitrary generatingArbitrary;
	private final Set<Float> specials = new LinkedHashSet<>();

	public DefaultFloatArbitrary() {
		this.generatingArbitrary = new DecimalGeneratingArbitrary(Range.of(toBigDecimal(DEFAULT_MIN), toBigDecimal(DEFAULT_MAX)));
	}

	@Override
	public RandomGenerator<Float> generator(int genSize) {
		return arbitrary().generator(genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<Float>> exhaustive(long maxNumberOfSamples) {
		return arbitrary().exhaustive(maxNumberOfSamples);
	}

	@Override
	public EdgeCases<Float> edgeCases(int maxEdgeCases) {
		return arbitrary().edgeCases(maxEdgeCases);
	}

	@Override
	public Arbitrary<Float> edgeCases(Consumer<EdgeCases.Config<Float>> configurator) {
		Consumer<EdgeCases.Config<BigDecimal>> decimalConfigurator = new MappedEdgeCasesConsumer<>(
				configurator,
				BigDecimal::floatValue,
				BigDecimal::valueOf
		);
		DefaultFloatArbitrary clone = typedClone();
		clone.generatingArbitrary = (DecimalGeneratingArbitrary) generatingArbitrary.edgeCases(decimalConfigurator);
		return clone;
	}

	@Override
	public FloatArbitrary withDistribution(final RandomDistribution distribution) {
		DefaultFloatArbitrary clone = typedClone();
		clone.generatingArbitrary.distribution = distribution;
		return clone;
	}

	@Override
	public FloatArbitrary between(float min, boolean minIncluded, float max, boolean maxIncluded) {
		DefaultFloatArbitrary clone = typedClone();
		clone.generatingArbitrary.range = Range.of(toBigDecimal(min), minIncluded, toBigDecimal(max), maxIncluded);
		return clone;
	}

	@Override
	public FloatArbitrary greaterOrEqual(float min) {
		return between(min, true, generatingArbitrary.range.max.floatValue(), generatingArbitrary.range.maxIncluded);
	}

	@Override
	public FloatArbitrary greaterThan(float min) {
		return between(min, false, generatingArbitrary.range.max.floatValue(), generatingArbitrary.range.maxIncluded);
	}

	@Override
	public FloatArbitrary lessOrEqual(float max) {
		return between(generatingArbitrary.range.min.floatValue(), generatingArbitrary.range.minIncluded, max, true);
	}

	@Override
	public FloatArbitrary lessThan(float max) {
		return between(generatingArbitrary.range.min.floatValue(), generatingArbitrary.range.minIncluded, max, false);
	}

	@Override
	public FloatArbitrary ofScale(int scale) {
		DefaultFloatArbitrary clone = typedClone();
		clone.generatingArbitrary.scale = scale;
		return clone;
	}

	@Override
	public FloatArbitrary shrinkTowards(float target) {
		DefaultFloatArbitrary clone = typedClone();
		clone.generatingArbitrary.shrinkingTarget = BigDecimal.valueOf(target);
		return clone;
	}

	@Override
	public FloatArbitrary withSpecialValue(float special) {
		DefaultFloatArbitrary clone = typedClone();
		clone.specials.add(special);
		return clone;
	}

	@Override
	public FloatArbitrary withStandardSpecialValues() {
		DefaultFloatArbitrary clone = typedClone();
		clone.specials.add(Float.NaN);
		clone.specials.add(Float.MIN_VALUE);
		clone.specials.add(Float.MIN_NORMAL);
		clone.specials.add(Float.POSITIVE_INFINITY);
		clone.specials.add(Float.NEGATIVE_INFINITY);
		return clone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultFloatArbitrary that = (DefaultFloatArbitrary) o;

		if (!generatingArbitrary.equals(that.generatingArbitrary)) return false;
		return specials.equals(that.specials);
	}

	@Override
	public int hashCode() {
		int result = generatingArbitrary.hashCode();
		result = 31 * result + specials.hashCode();
		return result;
	}

	private Arbitrary<Float> arbitrary() {
		Arbitrary<Float> floatArbitrary = generatingArbitrary.map(BigDecimal::floatValue);
		if (specials.isEmpty()) {
			return floatArbitrary;
		}
		Arbitrary<Float> specialsArbitrary =
			Arbitraries.of(specials).edgeCases(c -> c.add(specials.toArray(new Float[0])));
		return Arbitraries.frequencyOf(
			Tuple.of(49, floatArbitrary),
			Tuple.of(1, specialsArbitrary)
		);
	}

	private BigDecimal toBigDecimal(float value) {
		return new BigDecimal(Float.toString(value));
	}

}
