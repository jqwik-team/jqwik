package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.5")
@FunctionalInterface
public interface RegistrarHook extends LifecycleHook {

	void registerHooks(RegistrarHook.Registrar registrar);

	interface Registrar {
		void register(Class<? extends LifecycleHook> hook, PropagationMode propagationMode);

		default void register(Class<? extends LifecycleHook> hook) {
			this.register(hook, PropagationMode.NOT_SET);
		};
	}

}
