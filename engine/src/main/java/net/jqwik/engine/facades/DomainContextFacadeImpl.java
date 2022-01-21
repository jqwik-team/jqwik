package net.jqwik.engine.facades;

import java.util.*;

import net.jqwik.api.configurators.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.properties.*;

public class DomainContextFacadeImpl extends DomainContext.DomainContextFacade {

	@Override
	public DomainContext global() {
		return CurrentDomainContext.GLOBAL_DOMAIN_CONTEXT;
	}

	@Override
	public Collection<ArbitraryProvider> getArbitraryProviders(DomainContextBase base, int priority) {
		return DomainContextBaseProviders.forContextBase(base, priority);
	}

	@Override
	public Collection<ArbitraryConfigurator> getArbitraryConfigurators(DomainContextBase base) {
		return DomainContextBaseConfigurators.forContextBase(base);
	}

}
