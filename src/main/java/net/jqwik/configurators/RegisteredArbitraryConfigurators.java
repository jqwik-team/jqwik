package net.jqwik.configurators;

import java.util.*;

import net.jqwik.api.configurators.*;

public class RegisteredArbitraryConfigurators {

	private static List<ArbitraryConfigurator> registeredConfigurators;

	public static List<ArbitraryConfigurator> getConfigurators() {
		if (null == registeredConfigurators) {
			loadArbitraryConfigurators();
		}
		return Collections.unmodifiableList(new ArrayList<>(registeredConfigurators));
	}

	private static void loadArbitraryConfigurators() {
		registeredConfigurators = new ArrayList<>();
		Iterable<ArbitraryConfigurator> providers = ServiceLoader.load(ArbitraryConfigurator.class);
		for (ArbitraryConfigurator provider : providers) {
			register(provider);
		}
	}

	public static void register(ArbitraryConfigurator configurator) {
		if (getConfigurators().contains(configurator)) {
			return;
		}
		registeredConfigurators.add(0, configurator);
	}

	public static void unregister(ArbitraryConfigurator configuratorToDelete) {
		getConfigurators().stream() //
				.filter(provider -> provider == configuratorToDelete) //
				.forEach(provider -> registeredConfigurators.remove(provider));
	}

	public static void unregister(Class<? extends ArbitraryConfigurator> configuratorClass) {
		getConfigurators().stream() //
				.filter(provider -> provider.getClass() == configuratorClass) //
				.forEach(provider -> registeredConfigurators.remove(provider));
	}

}
