package net.jqwik.engine.providers;

import java.util.*;

import net.jqwik.api.providers.*;

public class RegisteredArbitraryProviders {

	private static List<ArbitraryProvider> registeredProviders;

	public static List<ArbitraryProvider> getProviders() {
		if (null == registeredProviders) {
			loadArbitraryProviders();
		}
		return Collections.unmodifiableList(new ArrayList<>(registeredProviders));
	}

	private static void loadArbitraryProviders() {
		registeredProviders = new ArrayList<>();
		Iterable<ArbitraryProvider> providers = ServiceLoader.load(ArbitraryProvider.class);
		for (ArbitraryProvider provider : providers) {
			register(provider);
		}
	}

	public static void register(ArbitraryProvider provider) {
		if (getProviders().contains(provider)) {
			return;
		}
		registeredProviders.add(0, provider);
	}

	public static void unregister(ArbitraryProvider providerToDelete) {
		getProviders().stream() //
				.filter(provider -> provider == providerToDelete) //
				.forEach(provider -> registeredProviders.remove(provider));
	}

	public static void unregister(Class<? extends ArbitraryProvider> providerClass) {
		getProviders().stream() //
				.filter(provider -> provider.getClass() == providerClass) //
				.forEach(provider -> registeredProviders.remove(provider));
	}

}
