package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.4")
@FunctionalInterface
public interface ResolveParameterHook extends LifecycleHook {

	@API(status = EXPERIMENTAL, since = "1.2.5")
	@FunctionalInterface
	interface ParameterSupplier {
		/**
		 * Supply the requested parameter. For the <em>same</em> lifecycleContext object the <em>same</em>
		 * object must be returned if this object has state that could change its behaviour.
		 *
		 * @param lifecycleContext Is of type {@linkplain TryLifecycleContext} if used for a property parameter
		 * @return the freshly generated object or one stored for the same context
		 */
		Object get(LifecycleContext lifecycleContext);
	}

	/**
	 * This method will be called only once per property, whereas the returned supplier's get method
	 * is usually invoked for each try - and potentially more often during shrinking or when resolving
	 * parameters in before/after methods.
	 *
	 * @param parameterContext
	 * @return a supplier that should always return an equivalent object,
	 * i.e. an object that behaves the same when used in the same way.
	 */
	@API(status = EXPERIMENTAL, since = "1.2.5")
	Optional<ParameterSupplier> resolve(ParameterResolutionContext parameterContext);

	ResolveParameterHook DO_NOT_RESOLVE = ((parameterContext) -> Optional.empty());

}
