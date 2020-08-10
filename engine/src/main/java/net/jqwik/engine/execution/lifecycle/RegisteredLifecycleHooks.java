package net.jqwik.engine.execution.lifecycle;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.LazyServiceLoaderCache;

public class RegisteredLifecycleHooks {
	private static final LazyServiceLoaderCache<LifecycleHook> serviceCache = new LazyServiceLoaderCache(LifecycleHook.class);

	public static Iterable<LifecycleHook> getRegisteredHooks() {
		return serviceCache.getServices();
	}
}
