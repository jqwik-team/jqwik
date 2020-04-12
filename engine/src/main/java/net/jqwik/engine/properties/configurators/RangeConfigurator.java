package net.jqwik.engine.properties.configurators;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

import java.math.*;
import java.util.function.*;

public class RangeConfigurator extends ArbitraryConfiguratorBase {

	public BigDecimalArbitrary configure(BigDecimalArbitrary arbitrary, BigRange range) {
		BigDecimal min = evaluate(range.min(), BigDecimal::new);
		BigDecimal max = evaluate(range.max(), BigDecimal::new);
		return arbitrary.between(min, range.minIncluded(), max, range.maxIncluded());
	}

	public BigIntegerArbitrary configure(BigIntegerArbitrary arbitrary, BigRange range) {
		BigInteger min = evaluate(range.min(), val -> new BigDecimal(val).toBigIntegerExact());
		BigInteger max = evaluate(range.max(), val -> new BigDecimal(val).toBigIntegerExact());
		if (!range.minIncluded() || !range.maxIncluded()) {
			String message = "minIncluded and maxIncluded are only allowed for parameters of type java.math.BigDecimal";
			throw new JqwikException(message);
		}
		return arbitrary.greaterOrEqual(min).lessOrEqual(max);
	}

	private static <T> T evaluate(String valueString, Function<String, T> evaluator) {
		return valueString.isEmpty() ? null : evaluator.apply(valueString);
	}

	public ByteArbitrary configure(ByteArbitrary arbitrary, ByteRange range) {
		return arbitrary.greaterOrEqual(range.min()).lessOrEqual(range.max());
	}

	public DoubleArbitrary configure(DoubleArbitrary arbitrary, DoubleRange range) {
		return arbitrary.between(range.min(), range.minIncluded(), range.max(), range.maxIncluded());
	}

	public FloatArbitrary configure(FloatArbitrary arbitrary, FloatRange range) {
		return arbitrary.between(range.min(), range.minIncluded(), range.max(), range.maxIncluded());
	}

	public IntegerArbitrary configure(IntegerArbitrary arbitrary, IntRange range) {
		return arbitrary.greaterOrEqual(range.min()).lessOrEqual(range.max());
	}

	public LongArbitrary configure(LongArbitrary arbitrary, LongRange range) {
		return arbitrary.greaterOrEqual(range.min()).lessOrEqual(range.max());
	}

	public ShortArbitrary configure(ShortArbitrary arbitrary, ShortRange range) {
		return arbitrary.greaterOrEqual(range.min()).lessOrEqual(range.max());
	}

}
