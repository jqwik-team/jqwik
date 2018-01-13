package net.jqwik.providers;

import java.util.*;

import net.jqwik.api.providers.*;

public class DefaultArbitraryProviders {

	private static List<ArbitraryProvider> defaultProviders;

	public static List<ArbitraryProvider> getProviders() {
		if (null == defaultProviders) {
			loadArbitraryProviders();
		}
		return Collections.unmodifiableList(new ArrayList<>(defaultProviders));
	}

	private static void loadArbitraryProviders() {
		defaultProviders = new ArrayList<>();
		Iterable<ArbitraryProvider> providers = ServiceLoader.load(ArbitraryProvider.class);
		for (ArbitraryProvider provider : providers) {
			register(provider);
		}
	}

	public static void register(ArbitraryProvider provider) {
		if (getProviders().contains(provider)) {
			return;
		}
		defaultProviders.add(0, provider);
	}

	public static void unregister(ArbitraryProvider providerToDelete) {
		getProviders().stream() //
				.filter(provider -> provider == providerToDelete) //
				.forEach(provider -> defaultProviders.remove(provider));
	}

	public static void unregister(Class<? extends ArbitraryProvider> providerClass) {
		getProviders().stream() //
				.filter(provider -> provider.getClass() == providerClass) //
				.forEach(provider -> defaultProviders.remove(provider));
	}

}
