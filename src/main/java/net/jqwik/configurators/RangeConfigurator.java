package net.jqwik.configurators;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

import java.math.*;

public class RangeConfigurator extends ArbitraryConfiguratorBase {

	public BigDecimalArbitrary configure(BigDecimalArbitrary arbitrary, DoubleRange range) {
		return arbitrary.withMin(new BigDecimal(range.min())).withMax(new BigDecimal(range.max()));
	}

	public BigIntegerArbitrary configure(BigIntegerArbitrary arbitrary, LongRange range) {
		return arbitrary.withMin(BigInteger.valueOf(range.min())).withMax(BigInteger.valueOf(range.max()));
	}

	public ByteArbitrary configure(ByteArbitrary arbitrary, ByteRange range) {
		return arbitrary.withMin(range.min()).withMax(range.max());
	}

	public DoubleArbitrary configure(DoubleArbitrary arbitrary, DoubleRange range) {
		return arbitrary.withMin(range.min()).withMax(range.max());
	}

	public FloatArbitrary configure(FloatArbitrary arbitrary, FloatRange range) {
		return arbitrary.withMin(range.min()).withMax(range.max());
	}

	public IntegerArbitrary configure(IntegerArbitrary arbitrary, IntRange range) {
		return arbitrary.withMin(range.min()).withMax(range.max());
	}

	public LongArbitrary configure(LongArbitrary arbitrary, LongRange range) {
		return arbitrary.withMin(range.min()).withMax(range.max());
	}

	public ShortArbitrary configure(ShortArbitrary arbitrary, ShortRange range) {
		return arbitrary.withMin(range.min()).withMax(range.max());
	}

	public CharacterArbitrary configure(CharacterArbitrary arbitrary, CharRange range) {
		return arbitrary.between(range.min(), range.max());
	}

}
