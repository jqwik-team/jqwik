package net.jqwik.engine;

import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

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
