package net.jqwik.engine.facades;

import java.util.*;

import net.jqwik.api.configurators.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.configurators.*;
import net.jqwik.engine.providers.*;

public class DomainContextFacadeImpl extends DomainContext.DomainContextFacade {

	public static final ThreadLocal<DomainContext> currentContext = new ThreadLocal<>();

	private DomainContext global = new GlobalDomainContext();

	@Override
	public DomainContext global() {
		return global;
	}

	private static class GlobalDomainContext implements DomainContext {

		@Override
		public List<ArbitraryProvider> getArbitraryProviders() {
			return RegisteredArbitraryProviders.getProviders();
		}

		@Override
		public List<ArbitraryConfigurator> getArbitraryConfigurators() {
			return RegisteredArbitraryConfigurators.getConfigurators();
		}
	}
}
