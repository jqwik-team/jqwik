package net.jqwik.engine.facades;

import java.util.*;

import net.jqwik.api.configurators.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.*;

public class DomainContextBaseFacadeImpl extends DomainContextBase.DomainContextBaseFacade {

	@Override
	public List<ArbitraryProvider> getArbitraryProviders(DomainContextBase base, int priority) {
		return DomainContextBaseProviders.forContextBase(base, priority);
	}

	@Override
	public List<ArbitraryConfigurator> getArbitraryConfigurators(DomainContextBase base) {
		return Collections.emptyList();
	}

}
