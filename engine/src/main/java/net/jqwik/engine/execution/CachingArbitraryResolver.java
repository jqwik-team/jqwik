package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;

// Make sure that all property parameters are only resolved once per property run
public class CachingArbitraryResolver implements ArbitraryResolver {

	private final Map<Parameter, Set<Arbitrary<?>>> cache = new LinkedHashMap<>();

	private final ArbitraryResolver resolver;

	public CachingArbitraryResolver(ArbitraryResolver arbitraryResolver) {
		resolver = arbitraryResolver;
	}

	@Override
	public Set<Arbitrary<?>> forParameter(MethodParameter parameter) {
		return cache.computeIfAbsent(
			parameter.getRawParameter(),
			ignore -> resolver.forParameter(parameter)
		);
	}
}
