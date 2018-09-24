package net.jqwik.api.configurators;

import java.lang.annotation.*;
import java.util.*;

import net.jqwik.api.*;

/**
 * Implementors can modify any arbitrary before it's being used for value generation.
 * Most implementations use {@linkplain ArbitraryConfiguratorBase} to derive from
 *
 * <p>
 * Implementations must be registered in <code>/META-INF/services/net.jqwik.api.configurators.ArbitraryConfigurator</code>
 * so that they will be automatically considered for arbitrary configuration.
 * <p>
 */
public interface ArbitraryConfigurator extends Comparable<ArbitraryConfigurator> {

	/**
	 * Configure a given {@code arbitrary} and return the configured instance
	 * which can be the original instance or a different one with the same
	 * parameter type {@code T}.
	 *
	 * @param arbitrary
	 * @param annotations
	 * @param <T>
	 * @return the newly configured arbitrary instance
	 */
	<T> Arbitrary<T> configure(Arbitrary<T> arbitrary, List<Annotation> annotations);

	/**
	 * Determines the order in which a configurator will be applied in regards to other configurators.
	 * Default value is {@code 100}. Use lower values to enforce earlier application and
	 * higher values for later application.
	 *
	 * @return the order
	 */
	default int order() {
		return 100;
	}

	@Override
	default int compareTo(ArbitraryConfigurator other) {
		return Integer.compare(this.order(), other.order());
	}
}
