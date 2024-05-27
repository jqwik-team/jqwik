package net.jqwik.api.providers;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Implementations of this class are used to provide default arbitraries to
 * {@code ForAll} parameters without an explicit provider reference.
 * <p>
 * Implementations must be registered in <code>/META-INF/services/net.jqwik.api.providers.ArbitraryProvider</code>
 * so that they will be automatically considered for parameter resolution.
 * </p>
 * <p>
 * Some examples that come with jqwik:
 * </p>
 * <ul>
 * <li><a href="https://github.com/jqwik-team/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/providers/EnumArbitraryProvider.java"
 * >net.jqwik.engine.providers.EnumArbitraryProvider</a></li>
 * <li><a href="https://github.com/jqwik-team/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/providers/BigDecimalArbitraryProvider.java"
 * >net.jqwik.engine.providers.BigDecimalArbitraryProvider</a></li>
 * <li><a href="https://github.com/jqwik-team/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/providers/ListArbitraryProvider.java"
 * >net.jqwik.engine.providers.ListArbitraryProvider</a></li>
 * </ul>
 */
@API(status = MAINTAINED, since = "1.0")
public interface ArbitraryProvider {

	@FunctionalInterface
	interface SubtypeProvider extends Function<TypeUsage, Set<Arbitrary<?>>> {

		/**
		 * Resolve all typeUsages and return a stream of all possible arbitraries
		 * combinations per type. The list of arbitraries returned in the
		 * stream has the same size as the number of typeUsages handed in.
		 *
		 * @param typeUsages
		 * @return stream of list of arbitraries
		 */
		@API(status = MAINTAINED, since = "1.2.0")
		default Stream<List<Arbitrary<?>>> resolveAndCombine(TypeUsage... typeUsages) {

			List<Arbitrary<Arbitrary<?>>> arbitraries =
				Arrays.stream(typeUsages)
					  .map((TypeUsage typeUsage) ->
							   Arbitraries.of(new ArrayList<>(this.apply(typeUsage))))
					  .collect(Collectors.toList());

			Optional<Stream<List<Arbitrary<?>>>> optionalArbitraries =
				Combinators
					.combine(arbitraries)
					.as(as -> as)
					.allValues();

			return optionalArbitraries.orElse(Stream.empty());
		}

		/**
		 * Convenience method to combine set of arbitraries in optional choice-based arbitrary.
		 *
		 * @param typeUsage
		 * @return Optional arbitrary instance
		 */
		@API(status = EXPERIMENTAL, since = "1.5.2")
		default Optional<Arbitrary<?>> provideOneFor(TypeUsage typeUsage) {
			Set<Arbitrary<?>> choices = this.apply(typeUsage);
			if (choices.isEmpty()) {
				return Optional.empty();
			}
			return Optional.of(Arbitraries.oneOf(choices));
		}

	}

	/**
	 * Return true if the provider is suitable for {@code targetType}
	 */
	boolean canProvideFor(TypeUsage targetType);

	/**
	 * This is the method you must override in your own implementations of {@code ArbitraryProvider}.
	 * It should return a set of arbitrary instances for a given {@code targetType}.
	 * <p>
	 * Only {@code targetType}s that have been allowed by {@linkplain #canProvideFor(TypeUsage)}
	 * will be given to this method.
	 * </p>
	 * <p>
	 * For each try a single, randomly chosen element of the set will be used to generate
	 * all objects represented by this arbitrary. This is necessary in order to make
	 * generation of parameterized types stable.
	 * </p>
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
	 */
	default int priority() {
		return 0;
	}
}
