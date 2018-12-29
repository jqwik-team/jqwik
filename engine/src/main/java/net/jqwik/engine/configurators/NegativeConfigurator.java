package net.jqwik.engine.configurators;

import java.math.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

public class NegativeConfigurator extends ArbitraryConfiguratorBase {

	public Arbitrary<BigDecimal> configure(BigDecimalArbitrary arbitrary, Negative negative) {
		return arbitrary.lessOrEqual(BigDecimal.valueOf(-1)).filter(value -> value.compareTo(BigDecimal.ZERO) < 0);
	}

	public BigIntegerArbitrary configure(BigIntegerArbitrary arbitrary, Negative negative) {
		return arbitrary.lessOrEqual(BigInteger.valueOf(-1));
	}

	public ByteArbitrary configure(ByteArbitrary arbitrary, Negative negative) {
		return arbitrary.lessOrEqual((byte) -1);
	}

	public Arbitrary<Double> configure(DoubleArbitrary arbitrary, Negative negative) {
		return arbitrary.lessOrEqual(0.0).filter(value -> value < 0.0);
	}

	public Arbitrary<Float> configure(FloatArbitrary arbitrary, Negative negative) {
		return arbitrary.lessOrEqual(-0.0f).filter(value -> value < 0.0f);
	}

	public IntegerArbitrary configure(IntegerArbitrary arbitrary, Negative negative) {
		return arbitrary.lessOrEqual(-1);
	}

	public LongArbitrary configure(LongArbitrary arbitrary, Negative negative) {
		return arbitrary.lessOrEqual(-1L);
	}

	public ShortArbitrary configure(ShortArbitrary arbitrary, Negative negative) {
		return arbitrary.lessOrEqual((short) -1);
	}

}
