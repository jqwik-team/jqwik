package net.jqwik.execution.lifecycle;

import java.util.*;

import net.jqwik.api.lifecycle.*;

public class RegisteredLifecycleHooks {

	private static List<LifecycleHook> registeredHooks = null;

	public static synchronized Iterable<LifecycleHook> getRegisteredHooks() {
		// Cache hooks so that even if there are multiple executions
		// there won't be more than one instance of each LifecycleHook
		if (registeredHooks == null) {
			registeredHooks = new ArrayList<>();
			for (LifecycleHook lifecycleHook : ServiceLoader.load(LifecycleHook.class)) {
				registeredHooks.add(lifecycleHook);
			}
		}
		return registeredHooks;
	}

}
