package net.jqwik.api.configurators;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

/**
 * An {@linkplain Arbitrary} implementation can also implement this interface if it wants
 * to take over its own configuration which is usually being done by registered instances
 * of {@linkplain ArbitraryConfigurator}.
 *
 * <p>
 * There are a few implementors within jqwik's own codebase:
 * </p>
 * <ul>
 *     <li><a href="https://github.com/jlink/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/properties/arbitraries/OneOfArbitrary.java"
 *     >net.jqwik.engine.properties.arbitraries.OneOfArbitrary</a></li>
 *     <li><a href="https://github.com/jlink/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/properties/arbitraries/FrequencyOfArbitrary.java"
 *     >net.jqwik.engine.properties.arbitraries.FrequencyOfArbitrary</a></li>
 *     <li><a href="https://github.com/jlink/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/properties/arbitraries/ArrayArbitrary.java"
 *     >net.jqwik.engine.properties.arbitraries.ArrayArbitrary</a></li>
 * </ul>
 */
public interface SelfConfiguringArbitrary<T> {

	/**
	 * Do all configuration yourself or delegate to {@link ArbitraryConfigurator#configure(Arbitrary, TypeUsage)}
	 * of the {@code configurator} that's being handed in.
	 *
	 * @param configurator
	 * @param targetType
	 * @return
	 */
	Arbitrary<T> configure(ArbitraryConfigurator configurator, TypeUsage targetType);
}
