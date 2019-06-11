package net.jqwik.engine.providers;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

public class MapArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(Map.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		TypeUsage keyType = targetType.getTypeArgument(0);
		TypeUsage valueType = targetType.getTypeArgument(1);

		List<Arbitrary<?>> keyArbitraries = new ArrayList<>(subtypeProvider.apply(keyType));
		List<Arbitrary<?>> valueArbitraries = new ArrayList<>(subtypeProvider.apply(valueType));

		Set<Arbitrary<?>> providedArbitraries = new HashSet<>();

		Optional<Stream<Arbitrary>> optionalMapArbitraries =
			Combinators
				.combine(Arbitraries.of(keyArbitraries), Arbitraries.of(valueArbitraries))
				.as((keysArbitrary, valuesArbitrary) -> (Arbitrary) Arbitraries.maps(keysArbitrary, valuesArbitrary))
				.allValues();

		optionalMapArbitraries.ifPresent(stream -> stream.forEach(providedArbitraries::add));

		return providedArbitraries;

	}
}
