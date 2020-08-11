package net.jqwik.engine.properties.configurators;

import java.util.*;

import net.jqwik.api.configurators.*;
import net.jqwik.engine.support.*;

public class RegisteredArbitraryConfigurators {

	private static final LazyServiceLoaderCache<ArbitraryConfigurator> serviceCache = new LazyServiceLoaderCache<>(ArbitraryConfigurator.class);

	public static List<ArbitraryConfigurator> getConfigurators() {
		return Collections.unmodifiableList(serviceCache.getServices());
	}

	public static void register(ArbitraryConfigurator configurator) {
		if (serviceCache.getServices().contains(configurator)) {
			return;
		}
		serviceCache.getServices().add(0, configurator);
	}
}
