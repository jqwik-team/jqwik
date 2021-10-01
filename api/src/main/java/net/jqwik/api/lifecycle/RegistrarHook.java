package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use this hook if you want to apply several hook implementations that belong
 * together but that cannot be implemented in a single class, e.g. because
 * the same hook type must be added with different proximity or different propagation.
 */
@API(status = MAINTAINED, since = "1.4.0")
@FunctionalInterface
public interface RegistrarHook extends LifecycleHook {

	/**
	 * This method will be called during hook registration, i.e. before any test has been started.
	 * It allows to register as many other hook implementations as necessary.
	 *
	 * @param registrar the registrar to use for registering lifecycle hooks
	 */
	void registerHooks(RegistrarHook.Registrar registrar);

	/**
	 * A short-lived object used for registering concrete hook implementation classes.
	 */
	interface Registrar {

		/**
		 * Register a concrete hook implementation.
		 *
		 * @param hook a concrete hook implementation class
		 * @param propagationMode propagation enum
		 */
		void register(Class<? extends LifecycleHook> hook, PropagationMode propagationMode);

		/**
		 * Register a concrete hook implementation with its default {@linkplain PropagationMode}.
		 *
		 * @param hook a concrete hook implementation class
		 */
		default void register(Class<? extends LifecycleHook> hook) {
			this.register(hook, PropagationMode.NOT_SET);
		};
	}

}
