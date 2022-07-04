package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.*;

public class DefaultDoubleArbitrary extends TypedCloneable implements DoubleArbitrary {

	private static final double DEFAULT_MIN = -Double.MAX_VALUE;
	private static final double DEFAULT_MAX = Double.MAX_VALUE;

	private DecimalGeneratingArbitrary generatingArbitrary;
	private final Set<Double> specials = new LinkedHashSet<>();

	public DefaultDoubleArbitrary() {
		this.generatingArbitrary = new DecimalGeneratingArbitrary(Range.of(toBigDecimal(DEFAULT_MIN), toBigDecimal(DEFAULT_MAX)));
	}

	@Override
	public RandomGenerator<Double> generator(int genSize) {
		return arbitrary().generator(genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<Double>> exhaustive(long maxNumberOfSamples) {
		return arbitrary().exhaustive(maxNumberOfSamples);
	}

	@Override
	public EdgeCases<Double> edgeCases(int maxEdgeCases) {
		return arbitrary().edgeCases(maxEdgeCases);
	}

	@Override
	public Arbitrary<Double> edgeCases(Consumer<EdgeCases.Config<Double>> configurator) {
		Consumer<EdgeCases.Config<BigDecimal>> decimalConfigurator = new MappedEdgeCasesConsumer<>(
				configurator,
				BigDecimal::doubleValue,
				BigDecimal::valueOf
		);
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary = (DecimalGeneratingArbitrary) generatingArbitrary.edgeCases(decimalConfigurator);
		return clone;
	}

	@Override
	public DoubleArbitrary withDistribution(final RandomDistribution distribution) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary.distribution = distribution;
		return clone;
	}

	@Override
	public DoubleArbitrary between(double min, boolean minIncluded, double max, boolean maxIncluded) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary.range = Range.of(toBigDecimal(min), minIncluded, toBigDecimal(max), maxIncluded);
		return clone;
	}

	@Override
	public DoubleArbitrary greaterOrEqual(double min) {
		return between(min, true, generatingArbitrary.range.max.doubleValue(), generatingArbitrary.range.maxIncluded);
	}

	@Override
	public DoubleArbitrary greaterThan(double min) {
		return between(min, false, generatingArbitrary.range.max.doubleValue(), generatingArbitrary.range.maxIncluded);
	}

	@Override
	public DoubleArbitrary lessOrEqual(double max) {
		return between(generatingArbitrary.range.min.doubleValue(), generatingArbitrary.range.minIncluded, max, true);
	}

	@Override
	public DoubleArbitrary lessThan(double max) {
		return between(generatingArbitrary.range.min.doubleValue(), generatingArbitrary.range.minIncluded, max, false);
	}

	@Override
	public DoubleArbitrary ofScale(int scale) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary.scale = scale;
		return clone;
	}

	@Override
	public DoubleArbitrary shrinkTowards(double target) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.generatingArbitrary.shrinkingTarget = BigDecimal.valueOf(target);
		return clone;
	}

	@Override
	public DoubleArbitrary withSpecialValue(double special) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.specials.add(special);
		return clone;
	}

	@Override
	public DoubleArbitrary withStandardSpecialValues() {
		DefaultDoubleArbitrary clone = typedClone();
		clone.specials.add(Double.NaN);
		clone.specials.add(Double.MIN_VALUE);
		clone.specials.add(Double.MIN_NORMAL);
		clone.specials.add(Double.POSITIVE_INFINITY);
		clone.specials.add(Double.NEGATIVE_INFINITY);
		return clone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultDoubleArbitrary that = (DefaultDoubleArbitrary) o;

		if (!generatingArbitrary.equals(that.generatingArbitrary)) return false;
		return specials.equals(that.specials);
	}

	@Override
	public int hashCode() {
		int result = generatingArbitrary.hashCode();
		result = 31 * result + specials.hashCode();
		return result;
	}

	private BigDecimal toBigDecimal(double value) {
		return new BigDecimal(Double.toString(value));
	}

	private Arbitrary<Double> arbitrary() {
		Arbitrary<Double> doubleArbitrary = generatingArbitrary.map(BigDecimal::doubleValue);
		if (specials.isEmpty()) {
			return doubleArbitrary;
		}
		Arbitrary<Double> specialsArbitrary =
			Arbitraries.of(specials).edgeCases(c -> c.add(specials.toArray(new Double[0])));
		return Arbitraries.frequencyOf(
			Tuple.of(49, doubleArbitrary),
			Tuple.of(1, specialsArbitrary)
		);
	}
}
