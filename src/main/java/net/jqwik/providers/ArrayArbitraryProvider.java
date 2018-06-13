package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.util.*;
import java.util.function.*;

public class ArrayArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isArray();
	}

	@Override
	public Arbitrary<?> provideFor(TypeUsage targetType, Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider) {
		return targetType //
			.getComponentType() //
			.map(subtypeProvider) //
			.map(optionalArbitrary -> optionalArbitrary
				.map(elementArbitrary -> elementArbitrary.array(targetType.getRawType()))
				.orElse(null)) //
			.orElse(null);
	}

}
