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
	 */
	boolean canProvideFor(TypeUsage targetType);

	/**
	 * Return an arbitrary instance for a given {@code targetType}.
	 *
	 * Only {@code targetType}s that have been allowed by {@linkplain #canProvideFor(TypeUsage)}
	 * will be given to this method.
	 *
	 * {@code subtypeProvider} can be used to get the arbitraries for any type argument of {@code targetType}.
	 *
	 * {@link Deprecated Use {@linkplain #provideArbitrariesFor(TypeUsage, Function)} instead.}
	 *
	 * This method will be removed in one of the next versions of jqwik.
	 */
	@Deprecated
	Arbitrary<?> provideFor(TypeUsage targetType, Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider);

	/**
	 * Return a set of arbitrary instances for a given {@code targetType}.
	 *
	 * Only {@code targetType}s that have been allowed by {@linkplain #canProvideFor(TypeUsage)}
	 * will be given to this method.
	 *
	 * {@code subtypeProvider} can be used to get the arbitraries for any type argument of {@code targetType}.
	 */
	default Set<Arbitrary<?>> provideArbitrariesFor(TypeUsage targetType, Function<TypeUsage, Set<Arbitrary<?>>> subtypeProvider) {
		Function<TypeUsage, Optional<Arbitrary<?>>> subtypeOptionalProvider = typeUsage -> {
			Set<Arbitrary<?>> arbitraries = subtypeProvider.apply(typeUsage);
			if (arbitraries.isEmpty())
				return Optional.empty();
			else return Optional.of(arbitraries.iterator().next());
		};
		Arbitrary<?> arbitrary = provideFor(targetType, subtypeOptionalProvider);
		if (arbitrary == null)
			return Collections.emptySet();
		else
			return Collections.singleton(arbitrary);
	}
}
