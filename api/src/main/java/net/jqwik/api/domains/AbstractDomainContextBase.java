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

	private final List<ArbitraryProvider> providers = new ArrayList<>();
	private final List<ArbitraryConfigurator> configurators = new ArrayList<>();

	@Override
	public List<ArbitraryProvider> getArbitraryProviders() {
		return providers;
	}

	@Override
	public List<ArbitraryConfigurator> getArbitraryConfigurators() {
		return configurators;
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
				return targetType.canBeAssignedTo(registeredType);
			}

			@Override
			public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
				return Collections.singleton(arbitrary);
			}

			@Override
			public int priority() {
				// Replace jqwik's default providers
				return 1;
			}
		};
		registerProvider(provider);
	}

	protected void registerArbitrary(Class<?> registeredType, Arbitrary<?> arbitrary) {
		registerArbitrary(TypeUsage.of(registeredType), arbitrary);
	}

	protected void registerConfigurator(ArbitraryConfigurator configurator) {
		if (configurators.contains(configurator)) {
			return;
		}
		configurators.add(configurator);
	}

	@Override
	// Domain contexts with same class are considered equal since they are only
	// instantiated through default constructor
	public boolean equals(Object obj) {
		return this.getClass().equals(obj.getClass());
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}

	@Override
	public String toString() {
		return super.getClass().getName();
	}
}
