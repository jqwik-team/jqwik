package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;

class CombinedDomainContext implements DomainContext {

	private final List<ArbitraryProvider> providers = new ArrayList<>();
	private final List<ArbitraryConfigurator> configurators = new ArrayList<>();
	private final List<SampleReportingFormat> reportingFormats = new ArrayList<>();

	CombinedDomainContext(Set<DomainContext> domainContexts) {
		for (DomainContext domainContext : domainContexts) {
			providers.addAll(domainContext.getArbitraryProviders());
			configurators.addAll(domainContext.getArbitraryConfigurators());
			reportingFormats.addAll(domainContext.getReportingFormats());
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

	@Override
	public Collection<SampleReportingFormat> getReportingFormats() {
		return reportingFormats;
	}
}
