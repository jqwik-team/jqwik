package net.jqwik.api.domains;

import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Base class for convention based implementations of {@linkplain DomainContext}
 *
 * @see DomainContext
 */
@API(status = EXPERIMENTAL, since = "1.5.2")
public abstract class DomainContextBase implements DomainContext {

	// Have same priority as jqwik default providers
	private static final int DEFAULT_PRIORITY = 0;

	private int defaultPriority = DEFAULT_PRIORITY;

	@API(status = INTERNAL)
	public abstract static class DomainContextBaseFacade {
		private static DomainContextBaseFacade implementation;

		static {
			implementation = FacadeLoader.load(DomainContextBaseFacade.class);
		}

		public abstract List<ArbitraryProvider> getArbitraryProviders(DomainContextBase base, int priority);

		public abstract List<ArbitraryConfigurator> getArbitraryConfigurators(DomainContextBase base);

	}

	@Override
	public List<ArbitraryProvider> getArbitraryProviders() {
		return DomainContextBaseFacade.implementation.getArbitraryProviders(this, defaultPriority);
	}

	@Override
	public List<ArbitraryConfigurator> getArbitraryConfigurators() {
		return DomainContextBaseFacade.implementation.getArbitraryConfigurators(this);
	}

	@Override
	public void setDefaultPriority(int priority) {
		defaultPriority = priority;
	}

}
