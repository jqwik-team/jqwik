package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * This hook type allows to provide parameters for property methods
 * but also to annotated lifecycle methods.
 */
@API(status = MAINTAINED, since = "1.4.0")
@FunctionalInterface
public interface ResolveParameterHook extends LifecycleHook {

	/**
	 * A functional interface specialized on providing parameters to property methods
	 * and to annotated lifecycle methods.
	 */
	@FunctionalInterface
	interface ParameterSupplier {
		/**
		 * Supply the requested parameter. For the <em>same</em> {@code optionalTry} the <em>same</em>
		 * object must be returned if this object has state that could change its behaviour.
		 *
		 * @param optionalTry Contains a {@linkplain TryLifecycleContext} instance if used to supply a property method's parameter.
		 *                    Otherwise it's empty.
		 * @return the freshly generated object or one stored for the same context
		 */
		Object get(Optional<TryLifecycleContext> optionalTry);
	}

	/**
	 * This method will be called only once per property, whereas the returned supplier's get method
	 * is usually invoked for each try - and potentially more often during shrinking or when resolving
	 * parameters in before/after methods.
	 * The returned supplier should always return an equivalent object,
	 * i.e. an object that behaves the same when used in the same way.
	 *
	 * @param parameterContext Contains information of parameter to resolve
	 * @param lifecycleContext Can be of type {@linkplain ContainerLifecycleContext} or {@linkplain PropertyLifecycleContext}
	 * @return supplier instance wrapped in {@linkplain Optional} or {@linkplain Optional#empty()} if it cannot be resolved
	 */
	Optional<ParameterSupplier> resolve(ParameterResolutionContext parameterContext, LifecycleContext lifecycleContext);

	@API(status = INTERNAL)
	ResolveParameterHook DO_NOT_RESOLVE = ((parameterContext, lifecycleContext) -> Optional.empty());

}
