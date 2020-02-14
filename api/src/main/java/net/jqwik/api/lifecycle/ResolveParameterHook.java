package net.jqwik.api.lifecycle;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
@FunctionalInterface
public interface ResolveParameterHook extends LifecycleHook {

	/**
	 * This method will be called only once per property, whereas the returned
	 * supplier is usually called for each try - and potentially more often during shrinking.
	 *
	 * @param parameterContext
	 * @param propertyContext
	 * @return a supplier function that should always return an equivalent object,
	 * i.e. an object that behaves the same when used in the same way.
	 */
	Optional<Supplier<Object>> resolve(ParameterResolutionContext parameterContext, PropertyLifecycleContext propertyContext);

	ResolveParameterHook DO_NOT_RESOLVE = ((parameterContext, propertyContext) -> Optional.empty());

}
