package net.jqwik.api;

import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.1")
public interface DomainContext {

	static DomainContext global() {
		return DomainContextFacade.implementation.global();
	}

	@API(status = MAINTAINED, since = "1.1")
	class Global implements DomainContext {
		@Override
		public List<ArbitraryProvider> getArbitraryProviders() {
			return global().getArbitraryProviders();
		}

		@Override
		public List<ArbitraryConfigurator> getArbitraryConfigurators() {
			return global().getArbitraryConfigurators();
		}
	}

	@API(status = INTERNAL)
	abstract class DomainContextFacade {
		private static DomainContextFacade implementation;

		static {
			implementation = FacadeLoader.load(DomainContextFacade.class);
		}

		public abstract DomainContext global();
	}

	List<ArbitraryProvider> getArbitraryProviders();

	List<ArbitraryConfigurator> getArbitraryConfigurators();
}
