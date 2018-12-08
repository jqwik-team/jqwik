package net.jqwik.engine.providers;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

public class ArrayArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isArray();
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		TypeUsage componentType = targetType.getComponentType().orElse(TypeUsage.forType(Object.class));
		return subtypeProvider.apply(componentType) //
			.stream() //
			.map(elementArbitrary -> elementArbitrary.array(targetType.getRawType())) //
			.collect(Collectors.toSet());
	}
}
