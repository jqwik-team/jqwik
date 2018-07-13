package net.jqwik.api.configurators;

import net.jqwik.api.*;

import java.lang.annotation.*;
import java.util.*;

/**
 * An {@linkplain Arbitrary} implementation can also implement this interface if it wants
 * to take over its own configuration which is usually being done by registered instances
 * of {@linkplain ArbitraryConfigurator}.
 *
 * There is only a single implementor withing jqwik's own codebase:
 * @see net.jqwik.properties.arbitraries.OneOfArbitrary
 */
public interface SelfConfiguringArbitrary<T> {

	/**
	 * Do all configuration yourself or delegate to {@link ArbitraryConfigurator#configure(Arbitrary, List)}
	 * of the {@code configurator} that's being handed in.
	 *
	 * @param configurator
	 * @param annotations
	 * @return
	 */
	Arbitrary<T> configure(ArbitraryConfigurator configurator, List<Annotation> annotations);
}
