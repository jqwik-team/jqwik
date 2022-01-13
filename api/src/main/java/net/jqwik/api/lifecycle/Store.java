package net.jqwik.api.lifecycle;

import java.util.function.*;

import org.apiguardian.api.*;
import org.jetbrains.annotations.*;

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

	@API(status = INTERNAL, since = "1.6.3")
	void reset();

	@API(status = INTERNAL)
	abstract class StoreFacade {
		private static final Store.StoreFacade implementation;

		static {
			implementation = FacadeLoader.load(Store.StoreFacade.class);
		}

		public abstract <T> Store<T> create(Object identifier, Lifespan visibility, Supplier<T> initialValueSupplier, @Nullable Consumer<T> onClose);

		public abstract <T> Store<T> get(Object identifier);

		public abstract <T> Store<T> free(Supplier<T> initialValueSupplier);
	}

	/**
	 * Any value that implements this interface will automatically be closed when its store goes out of scope.
	 * That scope is defined by the store's {@linkplain #lifespan()}
	 */
	@API(status = EXPERIMENTAL, since = "1.6.3")
	interface CloseOnReset {
		void close() throws Exception;
	}

	/**
	 * Create a new store for storing and retrieving values and objects in lifecycle
	 * hooks and lifecycle-dependent methods.
	 *
	 * <p>
	 *     Stores are created with respect to the current test / property.
	 *     Therefore you _must not save created stores in member variables_,
	 *     unless the containing object is unique per test / property.
	 * </p>
	 *
	 * @param <T>         The type of object to store
	 * @param identifier  Any object to identify a store. Must be globally unique and stable, i.e. hashCode and equals must not change.
	 * @param lifespan    A stored object's lifespan
	 * @param initialValueSupplier Supplies the value to be used for initializing the store depending on its lifespan
	 * @return New store instance
	 */
	static <T> Store<T> create(Object identifier, Lifespan lifespan, Supplier<T> initialValueSupplier) {
		return create(identifier, lifespan, initialValueSupplier, null);
	}

	/**
	 * Create a new store for storing and retrieving values and objects in lifecycle
	 * hooks and lifecycle-dependent methods.
	 *
	 * <p>
	 *     Stores are created with respect to the current test / property.
	 *     Therefore you _must not save created stores in member variables_,
	 *     unless the containing object is unique per test / property.
	 * </p>
	 *
	 * @param <T>         The type of object to store
	 * @param identifier  Any object to identify a store. Must be globally unique and stable, i.e. hashCode and equals must not change.
	 * @param lifespan    A stored object's lifespan
	 * @param initialValueSupplier Supplies the value to be used for initializing the store depending on its lifespan
	 * @param onClose Code to execute when a store value is discarded
	 * @return New store instance
	 */
	@API(status = EXPERIMENTAL, since = "1.6.3")
	static <T> Store<T> create(Object identifier, Lifespan lifespan, Supplier<T> initialValueSupplier, @Nullable Consumer<T> onClose) {
		return StoreFacade.implementation.create(identifier, lifespan, initialValueSupplier, onClose);
	}

	/**
	 * Find an existing store or create a new one if it doesn't exist.
	 *
	 * <p>
	 *     Stores are created with respect to the current test / property.
	 *     Therefore you _must not save created stores in member variables_,
	 *     unless the containing object is unique per test / property.
	 * </p>
	 *
	 * @param <T>         The type of object to store
	 * @param identifier  Any object to identify a store. Must be globally unique and stable, i.e. hashCode and equals must not change.
	 * @param lifespan A stored object's lifespan
	 * @param initialValueSupplier Supplies the value to be used for initializing the store depending on its lifespan
	 * @return New or existing store instance
	 */
	static <T> Store<T> getOrCreate(Object identifier, Lifespan lifespan, Supplier<T> initialValueSupplier) {
		return getOrCreate(identifier, lifespan, initialValueSupplier, null);
	}

	/**
	 * Find an existing store or create a new one if it doesn't exist.
	 *
	 * <p>
	 *     Stores are created with respect to the current test / property.
	 *     Therefore you _must not save created stores in member variables_,
	 *     unless the containing object is unique per test / property.
	 * </p>
	 *
	 * @param <T>         The type of object to store
	 * @param identifier  Any object to identify a store. Must be globally unique and stable, i.e. hashCode and equals must not change.
	 * @param lifespan A stored object's lifespan
	 * @param initialValueSupplier Supplies the value to be used for initializing the store depending on its lifespan
	 * @param onClose Code to execute when a store value is discarded
	 * @return New or existing store instance
	 */
	@API(status = EXPERIMENTAL, since = "1.6.3")
	static <T> Store<T> getOrCreate(Object identifier, Lifespan lifespan, Supplier<T> initialValueSupplier, @Nullable Consumer<T> onClose) {
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
			return Store.create(identifier, lifespan, initialValueSupplier, onClose);
		}
	}

	/**
	 * Retrieve a store that must be created somewhere else.
	 *
	 * @param identifier Any object to identify a store. Must be globally unique and stable, i.e. hashCode and equals must not change.
	 * @param <T>        The type of object to store
	 * @return Existing store instance
	 * @throws CannotFindStoreException
	 */
	static <T> Store<T> get(Object identifier) {
		return StoreFacade.implementation.get(identifier);
	}

	/**
	 * Create a "free" store, i.e. one that lives independently from a test run, property or try.
	 *
	 * @param <T>         The type of object to store
	 * @param initializer Supplies the value to be used for initializing the store depending on its lifespan
	 * @return New store instance
	 */
	@API(status = EXPERIMENTAL, since = "1.5.0")
	static <T> Store<T> free(Supplier<T> initializer) {
		return StoreFacade.implementation.free(initializer);
	}
}
