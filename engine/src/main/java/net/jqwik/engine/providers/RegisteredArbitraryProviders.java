package net.jqwik.engine.providers;

import java.util.*;

import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;

public class RegisteredArbitraryProviders {

	private static final LazyServiceLoaderCache<ArbitraryProvider> serviceCache = new LazyServiceLoaderCache<>(ArbitraryProvider.class);

	public static List<ArbitraryProvider> getProviders() {
		return Collections.unmodifiableList(new ArrayList<>(serviceCache.getServices()));
	}

	public static void register(ArbitraryProvider provider) {
		if (serviceCache.getServices().contains(provider)) {
			return;
		}
		serviceCache.getServices().add(0, provider);
	}

	public static void unregister(ArbitraryProvider providerToDelete) {
		List<ArbitraryProvider> services = serviceCache.getServices();
		services.stream()
				.filter(provider -> provider == providerToDelete)
				.forEach(services::remove);
	}

	public static void unregister(Class<? extends ArbitraryProvider> providerClass) {
		List<ArbitraryProvider> services = serviceCache.getServices();
		services.stream()
				.filter(provider -> provider.getClass() == providerClass)
				.forEach(services::remove);
	}
}
