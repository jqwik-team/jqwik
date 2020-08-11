package net.jqwik.engine.support;

import java.util.*;
import java.util.concurrent.*;

public class LazyServiceLoaderCache<S> {
	private final Class<S> clz;
	private List<S> services;

	public LazyServiceLoaderCache(Class<S> clz) {
		this.clz = clz;
	}

	public List<S> getServices() {
		if (services == null) {
			loadServices();
		}
		return services;
	}

	private synchronized void loadServices() {
		services = new CopyOnWriteArrayList<>();
		ServiceLoader.load(clz).forEach(services::add);
	}
}
