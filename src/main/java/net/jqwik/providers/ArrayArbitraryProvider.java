package net.jqwik.providers;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.util.*;
import java.util.stream.*;

public class ArrayArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isArray();
	}

	@Override
	public Set<Arbitrary<?>> provideArbitrariesFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		TypeUsage componentType = targetType.getComponentType().orElse(TypeUsage.forType(Object.class));
		return subtypeProvider.apply(componentType) //
			.stream() //
			.map(elementArbitrary -> elementArbitrary.array(targetType.getRawType())) //
			.collect(Collectors.toSet());
	}
}
