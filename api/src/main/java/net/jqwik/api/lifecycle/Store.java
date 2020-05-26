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

	Lifespan lifespan();

	void update(Function<T, T> updater);

	void reset();

	@API(status = EXPERIMENTAL, since = "1.2.4")
	Store<T> onClose(Consumer<T> onCloseCallback);

	@API(status = INTERNAL)
	abstract class StoreFacade {
		private static Store.StoreFacade implementation;

		static {
			implementation = FacadeLoader.load(Store.StoreFacade.class);
		}

		public abstract <T> Store<T> create(Object identifier, Lifespan visibility, Supplier<T> initializer);

		public abstract <T> Store<T> get(Object identifier);
	}

	/**
	 * Create a new store for storing and retrieving values and objects in lifecycle
	 * hooks and lifecycle-dependent methods.
	 *
	 * @param <T>         The type of object to store
	 * @param identifier  Any object to identify a store. Must be globally unique.
	 * @param lifespan    A stored object's lifespan
	 * @param initializer Supplies the value to be used for initializing the store depending on its lifespan
	 * @return New store instance
	 */
	static <T> Store<T> create(Object identifier, Lifespan lifespan, Supplier<T> initializer) {
		return StoreFacade.implementation.create(identifier, lifespan, initializer);
	}

	/**
	 * Find an existing store or create a new one if it doesn't exist
	 *
	 * @param <T>         The type of object to store
	 * @param identifier  Any object to identify a store. Must be globally unique.
	 * @param lifespan
	 * @param initializer Supplies the value to be used for initializing the store depending on its lifespan
	 * @return New or existing store instance
	 */
	static <T> Store<T> getOrCreate(Object identifier, Lifespan lifespan, Supplier<T> initializer) {
		try {
			Store<T> store = Store.get(identifier);
			if (!store.lifespan().equals(lifespan)) {
				String message = String.format(
					"Trying to recreate existing store [%s] with different lifespan [%s]",
					store,
					lifespan
				);
				throw new JqwikException(message);
			}
			return store;
		} catch (CannotFindStoreException cannotFindStore) {
			return Store.create(identifier, lifespan, initializer);
		}
	}

	/**
	 * Retrieve a store that must be created somewhere else.
	 *
	 * @param identifier Any object to identify a store. Must be globally unique.
	 * @param <T>        The type of object to store
	 * @return Existing store instance
	 * @throws CannotFindStoreException
	 */
	static <T> Store<T> get(Object identifier) {
		return StoreFacade.implementation.get(identifier);
	}

}
