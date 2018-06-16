package net.jqwik.api.configurators;

import net.jqwik.api.*;

import java.lang.annotation.*;
import java.util.*;

/**
 * This should be implemented by arbitraries who contain arbitraries themselves that should preserve
 * their capability to be configured by {@linkplain ArbitraryConfigurator} instances.
 */
public interface Configurable<T> {
	Arbitrary<T> configure(
		ArbitraryConfigurator configurator, List<Annotation> annotations
	);
}
