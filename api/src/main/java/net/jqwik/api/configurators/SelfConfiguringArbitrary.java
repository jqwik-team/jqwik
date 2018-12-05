package net.jqwik.api.configurators;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import java.lang.annotation.*;
import java.util.*;

/**
 * An {@linkplain Arbitrary} implementation can also implement this interface if it wants
 * to take over its own configuration which is usually being done by registered instances
 * of {@linkplain ArbitraryConfigurator}.
 *
 * There are a few implementors within jqwik's own codebase:
 * @see net.jqwik.properties.arbitraries.OneOfArbitrary
 * @see net.jqwik.properties.arbitraries.FrequencyOfArbitrary
 * @see net.jqwik.properties.arbitraries.ArrayArbitrary
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
