package net.jqwik.api.providers;

import net.jqwik.api.*;

import java.util.*;
import java.util.function.*;

/**
 * Implementation of this class are used to provide default arbitraries to
 * {@code ForAll} parameters without an explicit provider reference.
 * <p>
 * Implementations must be registered in <code>/META-INF/services/net.jqwik.api.providers.ArbitraryProvider</code>
 * so that they will be automatically considered for parameter resolution.
 * <p>
 * Some examples that come with jqwik:
 *
 * @see net.jqwik.providers.EnumArbitraryProvider
 * @see net.jqwik.providers.BigDecimalArbitraryProvider
 * @see net.jqwik.providers.ListArbitraryProvider
 */
public interface ArbitraryProvider {

	/**
	 * Return true if the provider is suitable for {@code targetType}
	 *
	 * This is a pre filter. Even if it returns {@code true}
	 * {@linkplain #provideFor(GenericType, Function)} can still return {@code null}
	 */
	boolean canProvideFor(GenericType targetType);

	/**
	 * Return an arbitrary instance or {@code null} for a given {@code targetType}.
	 * If {@code null} is returned the search for a fitting provider will continue.
	 *
	 * Only {@code targetType}s that have been allowed by {@linkplain #canProvideFor(GenericType)}
	 * will be given to this method.
	 *
	 * {@code subtypeProvider} can be used to get the arbitraries for any type argument of {@code targetType}.
	 */
	Arbitrary<?> provideFor(GenericType targetType, Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider);
}
