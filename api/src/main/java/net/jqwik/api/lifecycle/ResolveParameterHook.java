package net.jqwik.api.lifecycle;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
public interface ResolveParameterHook extends LifecycleHook {

	/**
	 * This method will be called only once per property, whereas the returned
	 * supplier is usually called for each try - and potentially more often during shrinking.
	 *
	 * @param parameterContext
	 * @return a supplier function that should always return an equivalent object,
	 * 			i.e. an object that behaves the same when used in the same way.
	 */
	Optional<Supplier<Object>> resolve(ParameterInjectionContext parameterContext);

	ResolveParameterHook DO_NOT_RESOLVE = (parameterContext -> Optional.empty());

	default int compareTo(ResolveParameterHook other) {
		return -Integer.compare(this.injectParameterPriority(), other.injectParameterPriority());
	}

	/**
	 * Higher values means higher priority
	 */
	default int injectParameterPriority() {
		return 0;
	}
}
