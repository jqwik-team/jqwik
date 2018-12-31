package net.jqwik.api.providers;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Implementation of this class are used to provide default arbitraries to
 * {@code ForAll} parameters without an explicit provider reference.
 * <p>
 * Implementations must be registered in <code>/META-INF/services/net.jqwik.api.providers.ArbitraryProvider</code>
 * so that they will be automatically considered for parameter resolution.
 * </p>
 * <p>
 * Some examples that come with jqwik:
 * </p>
 * <ul>
 *     <li><a href="https://github.com/jlink/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/providers/EnumArbitraryProvider.java"
 *     >net.jqwik.engine.providers.EnumArbitraryProvider</a></li>
 *     <li><a href="https://github.com/jlink/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/providers/BigDecimalArbitraryProvider.java"
 *     >net.jqwik.engine.providers.BigDecimalArbitraryProvider</a></li>
 *     <li><a href="https://github.com/jlink/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/providers/ListArbitraryProvider.java"
 *     >net.jqwik.engine.providers.ListArbitraryProvider</a></li>
 * </ul>
 */
@API(status = MAINTAINED, since = "1.0")
public interface ArbitraryProvider {

	@FunctionalInterface
	interface SubtypeProvider extends Function<TypeUsage, Set<Arbitrary<?>>> {
	}

	/**
	 * Return true if the provider is suitable for {@code targetType}
	 */
	boolean canProvideFor(TypeUsage targetType);

	/**
	 * This is the method you must override in your own implementations of {@code ArbitraryProvider}.
	 * It should return a set of arbitrary instances for a given {@code targetType}.
	 *
	 * Only {@code targetType}s that have been allowed by {@linkplain #canProvideFor(TypeUsage)}
	 * will be given to this method.
	 *
	 * {@code subtypeProvider} can be used to get the arbitraries for any type argument of {@code targetType}.
	 */
	Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider);

	/**
	 * Providers with higher priority will replace providers with lower priority. If there is more than one
	 * provider for a given type with the same priority, there results will add up in a single set of arbitraries
	 * to use.
	 *
	 * <ul>
	 * <li>Override with value &gt; 0 to replace most of _jqwik_'s default providers.</li>
	 * <li>Override with value &gt;  100 to replace arbitrary provisioning for unrestricted type variables and wildcard types.</li>
	 * <li>Override with value &gt;  100 to replace arbitrary provisioning for plain type {@code Object}.</li>
	 * </ul>
	 *
	 */
	default int priority() {
		return 0;
	}
}
