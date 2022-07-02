package net.jqwik.engine.providers;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.properties.arbitraries.*;

public class ArrayArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isArray();
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		TypeUsage componentType = targetType.getComponentType().orElse(TypeUsage.forType(Object.class));
		return subtypeProvider.apply(componentType)
			.stream()
			.map(elementArbitrary -> {
				if (!componentType.isTypeVariable()) {
					Class<?> arrayClass = targetType.getRawType();
					return elementArbitrary.array(arrayClass);
				} else {
					Class<?> componentClass = upperboundsSupertype(componentType);
					return DefaultArrayArbitrary.forComponentType(elementArbitrary, componentClass);
				}
			})
			.collect(CollectorsSupport.toLinkedHashSet());
	}

	private Class<?> upperboundsSupertype(TypeUsage componentType) {
		List<TypeUsage> upperBounds = componentType.getUpperBounds();
		if (upperBounds.size() != 1) {
			String message = String.format("jqwik cannot handle more than one upper bound in this case: <%s>", componentType);
			throw new JqwikException(message);
		}
		return upperBounds.get(0).getRawType();
	}
}
