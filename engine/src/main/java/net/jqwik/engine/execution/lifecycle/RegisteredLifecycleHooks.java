package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.lifecycle.*;

public class RegisteredLifecycleHooks {

	private static List<LifecycleHook> registeredHooks = null;

	public static synchronized Iterable<LifecycleHook> getRegisteredHooks() {
		if (registeredHooks == null) {
			registeredHooks = new ArrayList<>();
			for (LifecycleHook lifecycleHook : ServiceLoader.load(LifecycleHook.class)) {
				registeredHooks.add(lifecycleHook);
			}
		}
		return registeredHooks;
	}

}
