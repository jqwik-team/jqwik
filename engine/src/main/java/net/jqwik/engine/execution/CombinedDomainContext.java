package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.api.configurators.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;

class CombinedDomainContext implements DomainContext {

	private final List<ArbitraryProvider> providers = new ArrayList<>();
	private final List<ArbitraryConfigurator> configurators = new ArrayList<>();

	CombinedDomainContext(Set<DomainContext> domainContexts) {
		Set<DomainContext> expandedContexts = new HashSet<>(domainContexts);

		for (DomainContext domainContext : expandedContexts) {
			providers.addAll(domainContext.getArbitraryProviders());
			configurators.addAll(domainContext.getArbitraryConfigurators());
		}
		Collections.sort(configurators);
	}

	@Override
	public Collection<ArbitraryProvider> getArbitraryProviders() {
		return providers;
	}

	@Override
	public Collection<ArbitraryConfigurator> getArbitraryConfigurators() {
		return configurators;
	}
}
