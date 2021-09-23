package net.jqwik.engine.facades;

import java.util.*;

import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;

public class RegisteredTypeUsageEnhancers {

	private static final LazyServiceLoaderCache<TypeUsage.Enhancer> serviceCache = new LazyServiceLoaderCache<>(TypeUsage.Enhancer.class);

	public static List<TypeUsage.Enhancer> getEnhancers() {
		return Collections.unmodifiableList(serviceCache.getServices());
	}
}
