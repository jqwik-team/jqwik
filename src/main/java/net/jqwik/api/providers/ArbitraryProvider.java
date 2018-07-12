package net.jqwik.api.providers;

import net.jqwik.*;
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

	@FunctionalInterface
	interface SubtypeProvider extends Function<TypeUsage, Set<Arbitrary<?>>> {
	}

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
	 * {@link Deprecated Use {@linkplain #provideArbitrariesFor(TypeUsage, SubtypeProvider)} instead.}
	 *
	 * This method will be removed in version 0.9 of jqwik.
	 */
	@Deprecated
	default Arbitrary<?> provideFor(TypeUsage targetType, Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider) {
		throw new JqwikException(String.format("Please implement/override %s.provideArbitrariesFor()", getClass().getName()));
	}

	/**
	 * This is the method you must override in your own implementations of {@code ArbitraryProvider}.
	 * It should return a set of arbitrary instances for a given {@code targetType}.
	 *
	 * Only {@code targetType}s that have been allowed by {@linkplain #canProvideFor(TypeUsage)}
	 * will be given to this method.
	 *
	 * {@code subtypeProvider} can be used to get the arbitraries for any type argument of {@code targetType}.
	 */
	// TODO: Remove default implementation in jqwik 0.9
	default Set<Arbitrary<?>> provideArbitrariesFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
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

	/**
	 * Providers with higher priority will replace providers with lower priority. If there is more than one
	 * provider for a given type with the same priority, there results will add up in a single set of arbitraries
	 * to use.
	 *
	 * <ul>
	 * <li>Override with value > 0 to replace most of _jqwik_'s default providers.</li>
	 * <li>Override with value > 100 to replace arbitrary provisioning for unrestricted type variables and wildcard types.</li>
	 * <li>Override with value > 100 to replace arbitrary provisioning for plain type {@code Object}.</li>
	 * </ul>
	 *
	 */
	default int priority() {
		return 0;
	}
}
