package net.jqwik.api.facades;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = INTERNAL)
public abstract class ReflectionSupportFacade {

	public static final ReflectionSupportFacade implementation;

	static {
		implementation = FacadeLoader.load(ReflectionSupportFacade.class);
	}

	/**
	 * Create instance of a class that can potentially be a non static inner class
	 * and its outer instance might be {@code context}
	 *
	 * @param <T>     The type of the instance to create
	 * @param clazz   The class to instantiate
	 * @param context The potential context instance
	 * @return the newly created instance
	 */
	public abstract  <T> T newInstanceInTestContext(Class<T> clazz, Object context);
}
