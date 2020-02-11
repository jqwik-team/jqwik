package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
public interface InjectParameterHook extends LifecycleHook {

	Optional<Object> generateParameterValue(ParameterInjectionContext parameterContext, TryLifecycleContext tryLifecycleContext);

	InjectParameterHook INJECT_NOTHING = ((parameterContext, tryLifecycleContext) -> Optional.empty());

	default int compareTo(InjectParameterHook other) {
		return Integer.compare(this.injectParameterOrder(), other.injectParameterOrder());
	}

	/**
	 * Lower order value means earlier evaluation
	 */
	default int injectParameterOrder() {
		return 0;
	}
}
