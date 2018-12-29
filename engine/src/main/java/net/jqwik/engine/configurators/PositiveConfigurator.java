package net.jqwik.engine.configurators;

import java.math.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

public class PositiveConfigurator extends ArbitraryConfiguratorBase {

	public Arbitrary<BigDecimal> configure(BigDecimalArbitrary arbitrary, Positive positive) {
		return arbitrary.greaterOrEqual(BigDecimal.ZERO).filter(value -> value.compareTo(BigDecimal.ZERO) > 0);
	}

	public BigIntegerArbitrary configure(BigIntegerArbitrary arbitrary, Positive positive) {
		return arbitrary.greaterOrEqual(BigInteger.ONE);
	}

	public ByteArbitrary configure(ByteArbitrary arbitrary, Positive positive) {
		return arbitrary.greaterOrEqual((byte) 1);
	}

	public Arbitrary<Double> configure(DoubleArbitrary arbitrary, Positive positive) {
		return arbitrary.greaterOrEqual(0.0).filter(value -> value > 0.0);
	}

	public Arbitrary<Float> configure(FloatArbitrary arbitrary, Positive positive) {
		return arbitrary.greaterOrEqual(0.0f).filter(value -> value > 0.0f);
	}

	public IntegerArbitrary configure(IntegerArbitrary arbitrary, Positive positive) {
		return arbitrary.greaterOrEqual(1);
	}

	public LongArbitrary configure(LongArbitrary arbitrary, Positive positive) {
		return arbitrary.greaterOrEqual(1L);
	}

	public ShortArbitrary configure(ShortArbitrary arbitrary, Positive positive) {
		return arbitrary.greaterOrEqual((short) 1);
	}

}
