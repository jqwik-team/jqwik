package net.jqwik.api.domains;

import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.providers.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Class that implement this interface are used to annotate property methods or containers like this:
 * {@code Domain(MyDomainContext.class)}. They must have a constructor without parameters
 * to be usable in this way.
 *
 * <p>
 *     Lifecycle: Instantiate exactly once per property, then {@linkplain #initialize(PropertyLifecycleContext)}
 *     will be called before providers and configurators will be retrieved.
 * </p>
 *
 * <p>
 *     Most implementing class will subclass {@linkplain DomainContextBase}.
 * </p>
 *
 * @see Domain
 * @see DomainContextBase
 */

@API(status = MAINTAINED, since = "1.2.0")
@NonNullApi
public interface DomainContext {

	@API(status = INTERNAL)
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
		protected static DomainContextFacade implementation;

		static {
			implementation = FacadeLoader.load(DomainContextFacade.class);
		}

		public abstract DomainContext global();

		public abstract List<ArbitraryProvider> getArbitraryProviders(DomainContextBase base, int priority);

		public abstract List<ArbitraryConfigurator> getArbitraryConfigurators(DomainContextBase base);
	}

	List<ArbitraryProvider> getArbitraryProviders();

	List<ArbitraryConfigurator> getArbitraryConfigurators();

	/**
	 * This method will be called exactly once after instantiation of a given domain context class.
	 * The call will happen before any calls to {@linkplain #getArbitraryProviders()} and {@linkplain #getArbitraryConfigurators()}.
	 *
	 * <p>
	 * Override this message if your domain context needs access to the
	 * {@linkplain PropertyLifecycleContext context of a property}.
	 * </p>
	 */
	@API(status = EXPERIMENTAL, since = "1.5.4")
	default void initialize(PropertyLifecycleContext context) {
	}

}
