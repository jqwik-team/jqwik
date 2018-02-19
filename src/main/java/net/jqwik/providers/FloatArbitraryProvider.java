package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

import java.util.*;
import java.util.function.*;

public class FloatArbitraryProvider extends NullableArbitraryProvider {
	@Override
	public boolean canProvideFor(GenericType targetType) {
		return targetType.isCompatibleWith(Float.class);
	}

	@Override
	public Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider) {
		return Arbitraries.floats();
	}

	public FloatArbitrary configure(FloatArbitrary arbitrary, Scale scale) {
		return arbitrary.withScale(scale.value());
	}

	public FloatArbitrary configure(FloatArbitrary arbitrary, FloatRange range) {
		return arbitrary.withMin(range.min()).withMax(range.max());
	}

}
