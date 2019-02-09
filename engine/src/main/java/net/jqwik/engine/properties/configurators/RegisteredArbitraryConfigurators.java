package net.jqwik.engine.properties.configurators;

import java.util.*;

import net.jqwik.api.configurators.*;

public class RegisteredArbitraryConfigurators {

	private static List<ArbitraryConfigurator> registeredConfigurators;

	public static List<ArbitraryConfigurator> getConfigurators() {
		if (null == registeredConfigurators) {
			loadArbitraryConfigurators();
		}
		return Collections.unmodifiableList(registeredConfigurators);
	}

	private static void loadArbitraryConfigurators() {
		registeredConfigurators = new ArrayList<>();
		Iterable<ArbitraryConfigurator> providers = ServiceLoader.load(ArbitraryConfigurator.class);
		for (ArbitraryConfigurator provider : providers) {
			register(provider);
		}
		Collections.sort(registeredConfigurators);
	}

	public static void register(ArbitraryConfigurator configurator) {
		if (getConfigurators().contains(configurator)) {
			return;
		}
		registeredConfigurators.add(0, configurator);
	}

}
