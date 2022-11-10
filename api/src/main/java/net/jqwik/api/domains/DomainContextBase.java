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
 * <p>
 * In subclasses you can:
 *     <ul>
 *         <li>
 *          	Add methods annotated with {@linkplain Provide} and a return type of {@linkplain Arbitrary Arbitrary&lt;T&gt;}.
 *         		The result of an annotated method will then be used as an {@linkplain ArbitraryProvider arbitrary provider} for type {@code T}.
 *         		<br/>Those methods follow the same rules as provider methods in container classes,
 *         		i.e. they have an optional parameters of type {@linkplain TypeUsage}
 *         	    or with annotation {@linkplain ForAll}.
 *         	</li>
 *         <li>
 *          	Add inner classes (static or not static, but not private) that implement {@linkplain ArbitraryProvider}.
 *         		An instance of this class will then be used as {@linkplain ArbitraryProvider providers}.
 *         	</li>
 *         <li>
 *          	Add inner classes (static or not static, but not private) that implement {@linkplain ArbitraryConfigurator}.
 *         		An instance of this class will then be used as {@linkplain ArbitraryConfigurator configurator}.
 *         	</li>
 *     </ul>
 * </p>
 *
 * <p>
 *     Mind that a domain context does not automatically import global providers and configurators.
 *     If you want to have them available, you have to add the global domain context to the domain class or at the point of usage
 *     like this: {@code @Domain(DomainContext.Global.class)}.
 * </p>
 *
 * @see DomainContext
 * @see Provide
 */
@API(status = MAINTAINED, since = "1.5.2")
public abstract class DomainContextBase implements DomainContext {

	// Have higher priority than jqwik default providers
	private static final int DEFAULT_PRIORITY = 10;

	private int defaultPriority = DEFAULT_PRIORITY;

	@Override
	public Collection<ArbitraryProvider> getArbitraryProviders() {
		return DomainContextFacade.implementation.getArbitraryProviders(this, defaultPriority);
	}

	@Override
	public Collection<ArbitraryConfigurator> getArbitraryConfigurators() {
		return DomainContextFacade.implementation.getArbitraryConfigurators(this);
	}

	@Override
	public Collection<SampleReportingFormat> getReportingFormats() {
		return DomainContextFacade.implementation.getReportingFormats(this);
	}

	@Override
	public void setDefaultPriority(int priority) {
		defaultPriority = priority;
	}

}
