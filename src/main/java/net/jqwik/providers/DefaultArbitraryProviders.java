package net.jqwik.providers;

import net.jqwik.api.providers.*;

import java.util.*;

public class DefaultArbitraryProviders {


	private static List<ArbitraryProvider> defaultProviders;

	public static List<ArbitraryProvider> getProviders() {
		if (null == defaultProviders) {
			loadArbitraryProviders();
		}
		return Collections.unmodifiableList(defaultProviders);
	}

	private static void loadArbitraryProviders() {
		defaultProviders = new ArrayList<>();
		Iterable<ArbitraryProvider> providers = ServiceLoader.load(ArbitraryProvider.class);
		for (ArbitraryProvider provider : providers) {
			if (defaultProviders.contains(provider)) {
				continue;
			}
			defaultProviders.add(provider);
		}
	}

}
