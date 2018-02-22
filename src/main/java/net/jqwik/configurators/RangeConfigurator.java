package net.jqwik.configurators;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

import java.math.*;

public class RangeConfigurator extends ArbitraryConfiguratorBase {

	public BigDecimalArbitrary configure(BigDecimalArbitrary arbitrary, DoubleRange range) {
		return arbitrary.greaterOrEqual(new BigDecimal(range.min())).lessOrEqual(new BigDecimal(range.max()));
	}

	public BigIntegerArbitrary configure(BigIntegerArbitrary arbitrary, LongRange range) {
		return arbitrary.greaterOrEqual(BigInteger.valueOf(range.min())).lessOrEqual(BigInteger.valueOf(range.max()));
	}

	public ByteArbitrary configure(ByteArbitrary arbitrary, ByteRange range) {
		return arbitrary.greaterOrEqual(range.min()).lessOrEqual(range.max());
	}

	public DoubleArbitrary configure(DoubleArbitrary arbitrary, DoubleRange range) {
		return arbitrary.greaterOrEqual(range.min()).lessOrEqual(range.max());
	}

	public FloatArbitrary configure(FloatArbitrary arbitrary, FloatRange range) {
		return arbitrary.greaterOrEqual(range.min()).lessOrEqual(range.max());
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
