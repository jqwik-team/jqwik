package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.configurators.*;
import net.jqwik.engine.providers.*;

/**
 * Default providers are available even if the global domain context is not loaded
 */
public class DefaultArbitraries {

	public static List<ArbitraryProvider> getDefaultProviders() {
		ArrayList<ArbitraryProvider> providers = new ArrayList<>();
		providers.add(new EnumArbitraryProvider());
		providers.add(new ListArbitraryProvider());
		providers.add(new SetArbitraryProvider());
		providers.add(new HashMapArbitraryProvider());
		providers.add(new EntryArbitraryProvider());
		providers.add(new StreamArbitraryProvider());
		providers.add(new OptionalArbitraryProvider());
		providers.add(new ArrayArbitraryProvider());
		providers.add(new IteratorArbitraryProvider());
		providers.add(new UseTypeArbitraryProvider());
		providers.add(new FunctionArbitraryProvider());
		providers.add(new VoidArbitraryProvider());
		providers.add(new NullableArbitraryProvider());
		providers.add(new ArbitraryArbitraryProvider());
		return providers;
	}

	public static List<ArbitraryConfigurator> getDefaultConfigurators() {
		ArrayList<ArbitraryConfigurator> configurators = new ArrayList<>();
		configurators.add(new SizeConfigurator());
		configurators.add(new WithNullConfigurator());
		configurators.add(new UniqueElementsConfigurator());
		configurators.add(new UniqueCharsConfigurator());
		return configurators;
	}
}
