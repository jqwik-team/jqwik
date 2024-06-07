package net.jqwik.api.configurators;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * An {@linkplain Arbitrary} implementation can also implement this interface if it wants
 * to take over its own configuration which is usually being done by registered instances
 * of {@linkplain ArbitraryConfigurator}.
 *
 * <p>
 * There are a few implementors within jqwik's own codebase:
 * </p>
 * <ul>
 *     <li><a href="https://github.com/jqwik-team/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/properties/arbitraries/OneOfArbitrary.java"
 *     >net.jqwik.engine.properties.arbitraries.OneOfArbitrary</a></li>
 *     <li><a href="https://github.com/jqwik-team/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/properties/arbitraries/FrequencyOfArbitrary.java"
 *     >net.jqwik.engine.properties.arbitraries.FrequencyOfArbitrary</a></li>
 *     <li><a href="https://github.com/jqwik-team/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/properties/arbitraries/ArrayArbitrary.java"
 *     >net.jqwik.engine.properties.arbitraries.ArrayArbitrary</a></li>
 * </ul>
 */
@API(status = MAINTAINED, since = "1.0")
public interface SelfConfiguringArbitrary<T> {

	/**
	 * If an arbitrary is self configuring use it, otherwise use default configurator
	 */
	@SuppressWarnings("unchecked")
	@API(status = INTERNAL)
	static <T extends @Nullable Object> Arbitrary<T> configure(Arbitrary<T> self, ArbitraryConfigurator configurator, TypeUsage targetType) {
		if (self instanceof SelfConfiguringArbitrary) {
			return ((SelfConfiguringArbitrary<T>) self).configure(configurator, targetType);
		} else {
			return configurator.configure(self, targetType);
		}
	}

	/**
	 * Do all configuration yourself or delegate to {@link ArbitraryConfigurator#configure(Arbitrary, TypeUsage)}
	 * of the {@code configurator} that's being handed in.
	 *
	 * @param configurator the configurator to use
	 * @param targetType the target type of the arbitrary
	 * @return the arbitrary instance or a new derived one
	 */
	Arbitrary<T> configure(ArbitraryConfigurator configurator, TypeUsage targetType);
}
