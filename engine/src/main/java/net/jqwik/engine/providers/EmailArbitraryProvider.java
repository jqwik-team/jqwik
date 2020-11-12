package net.jqwik.engine.providers;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.providers.*;

import java.util.*;

public class EmailArbitraryProvider implements ArbitraryProvider {

	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		Optional<Email> annotation = targetType.findAnnotation(Email.class);
		return !annotation.equals(Optional.empty());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, ArbitraryProvider.SubtypeProvider subtypeProvider) {
		return Collections.singleton(Arbitraries.emails());
	}

}
