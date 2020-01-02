package net.jqwik.engine.facades;

import java.util.*;

import net.jqwik.api.configurators.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.properties.configurators.*;
import net.jqwik.engine.providers.*;

public class DomainContextFacadeImpl extends DomainContext.DomainContextFacade {

	private static final ThreadLocal<DomainContext> currentContext = new ThreadLocal<>();

	private static final DomainContext global = new GlobalDomainContext();

	public static DomainContext getCurrentContext() {
		if (currentContext.get() == null) {
			return global;
		}
		return currentContext.get();
	}

	public static void setCurrentContext(DomainContext context) {
		currentContext.set(context);
	}

	public static void removeCurrentContext() {
		currentContext.remove();
	}

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
