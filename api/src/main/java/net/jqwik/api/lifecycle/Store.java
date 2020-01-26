package net.jqwik.api.lifecycle;

import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.3")
public interface Store<T> {

	T get();

	void update(Function<T, T> updater);

	void reset();

	@API(status = INTERNAL)
	abstract class StoreFacade {
		private static Store.StoreFacade implementation;

		static {
			implementation = FacadeLoader.load(Store.StoreFacade.class);
		}

		public abstract <T> Store<T> create(Object identifier, Visibility visibility, Supplier<T> initializer);

		public abstract <T> Store<T> get(Object identifier);
	}

	/**
	 * A {@linkplain Store} with the same identifier can be visible
	 *
	 * <ul>
	 *     <li>globally, i.e. to all that have the identifier</li>
	 *     <li>locally, i.e. to all that have the identifier and
	 *     belong to the scoping element (a container class or a property method)
	 *     or its children</li>
	 * </ul>
	 */
	enum Visibility {
		GLOBAL, LOCAL
	}

	/**
	 * A {@linkplain Store} with the same identifier can live
	 *
	 * <ul>
	 *     <li>For the whole test run</li>
	 *     <li>For the currently running property</li>
	 *     <li>For the currently running try</li>
	 * </ul>
	 */
	enum Lifespan {
		TEST_RUN, CURRENT_PROPERTY, CURRENT_TRY
	}

	/**
	 * Create a new store for storing and retrieving values and objects in lifecycle
	 * hooks and lifecycle-dependent methods.
	 *
	 * @param <T>         The type of object to store
	 * @param identifier  Any object to identify a store. Must be globally unique.
	 * @param visibility
	 * @param initializer
	 * @return New store instance
	 */
	static <T> Store<T> create(Object identifier, Visibility visibility, Supplier<T> initializer) {
		return StoreFacade.implementation.create(identifier, visibility, initializer);
	}

	/**
	 * Retrieve a store that must be created somewhere else.
	 *
	 * @param identifier Any object to identify a store. Must be globally unique.
	 * @param <T>        The type of object to store
	 * @return New store instance
	 */
	static <T> Store<T> get(Object identifier) {
		return StoreFacade.implementation.get(identifier);
	}

}
