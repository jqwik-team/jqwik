package net.jqwik.api.domains;

import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Subclass for easier implementation of {@linkplain DomainContext}
 *
 * @see DomainContext
 */
@API(status = EXPERIMENTAL, since = "1.1")
public abstract class AbstractDomainContextBase implements DomainContext {

	// Have same priority as jqwik default providers
	private static final int DEFAULT_PRIORITY = 0;

	private final List<ArbitraryProvider> providers = new ArrayList<>();
	private final List<ArbitraryConfigurator> configurators = new ArrayList<>();

	private int priority = DEFAULT_PRIORITY;

	@Override
	public List<ArbitraryProvider> getArbitraryProviders() {
		return providers;
	}

	@Override
	public List<ArbitraryConfigurator> getArbitraryConfigurators() {
		return configurators;
	}

	@Override
	public void setDefaultPriority(int priority) {
		this.priority = priority;
	}

	protected void registerProvider(ArbitraryProvider provider) {
		if (providers.contains(provider)) {
			return;
		}
		providers.add(provider);
	}

	protected void registerArbitrary(TypeUsage registeredType, Arbitrary<?> arbitrary) {
		ArbitraryProvider provider = new ArbitraryProvider() {
			@Override
			public boolean canProvideFor(TypeUsage targetType) {
				return registeredType.canBeAssignedTo(targetType);
			}

			@Override
			public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
				return Collections.singleton(arbitrary);
			}

			@Override
			public int priority() {
				return priority;
			}
		};
		registerProvider(provider);
	}

	protected <T> void registerArbitrary(Class<T> registeredType, Arbitrary<T> arbitrary) {
		registerArbitrary(TypeUsage.of(registeredType), arbitrary);
	}

	protected void registerConfigurator(ArbitraryConfigurator configurator) {
		if (configurators.contains(configurator)) {
			return;
		}
		configurators.add(configurator);
	}
}
