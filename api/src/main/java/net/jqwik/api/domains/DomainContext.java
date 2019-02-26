package net.jqwik.api.domains;

import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.providers.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Class that implement this interface are used to annotate property methods or containers like this:
 * {@code Domain(MyDomainContext.class)}. They must have a constructor without parameters
 * to be usable this way.
 *
 * <p>
 *     Most implementing class will subclass {@linkplain AbstractDomainContextBase}.
 * </p>
 *
 * @see Domain
 * @see AbstractDomainContextBase
 */

@API(status = EXPERIMENTAL, since = "1.1")
public interface DomainContext {

	static DomainContext global() {
		return DomainContextFacade.implementation.global();
	}

	default void setDefaultPriority(int priority) {
		// ignore
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
