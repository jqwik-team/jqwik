package net.jqwik.engine.execution.lifecycle;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.lifecycle.*;

public class RegisteredLifecycleHooks {

	private static List<LifecycleHook> registeredHooks = null;

	public static synchronized Iterable<LifecycleHook> getRegisteredHooks(Function<String, Optional<String>> parameters) {
		if (registeredHooks == null) {
			registeredHooks = new ArrayList<>();
			for (LifecycleHook lifecycleHook : ServiceLoader.load(LifecycleHook.class)) {
				if (lifecycleHook instanceof LifecycleHook.Configurable) {
					((LifecycleHook.Configurable) lifecycleHook).configure(parameters);
				}
				registeredHooks.add(lifecycleHook);
			}
		}
		return registeredHooks;
	}

}
