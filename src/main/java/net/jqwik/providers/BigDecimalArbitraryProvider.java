package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

import java.math.*;
import java.util.*;
import java.util.function.*;

public class BigDecimalArbitraryProvider extends NullableArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isOfType(BigDecimal.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		return Arbitraries.bigDecimals();
	}

	public BigDecimalArbitrary configure(BigDecimalArbitrary arbitrary, Scale scale) {
		return arbitrary.withScale(scale.value());
	}

	public BigDecimalArbitrary configure(BigDecimalArbitrary arbitrary, DoubleRange range) {
		return arbitrary.withMin(new BigDecimal(range.min())).withMax(new BigDecimal(range.max()));
	}

}
