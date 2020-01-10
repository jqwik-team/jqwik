package net.jqwik.api.lifecycle;

import java.util.*;
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

		public abstract <T> Store<T> create(Visibility visibility, String name, Supplier<T> initializer);

		public abstract <T> Optional<Store<T>> get(String name, Class<T> type);
	}

	/**
	 * A {@linkplain Store} with the same name can be visible globally,
	 * for just the current test element or for the test element and its children
	 */
	enum Visibility {
		GLOBAL, LOCAL, CONTAINER
	}

	/**
	 *
	 * @param visibility
	 * @param name Must not be empty. Is case-sensitive. Will be trimmed.
	 * @param initializer
	 * @param <T>
	 * @return
	 */
	static <T> Store<T> create(Visibility visibility, String name, Supplier<T> initializer) {
		return StoreFacade.implementation.create(visibility, name, initializer);
	}

	static <T> Optional<Store<T>> get(String name, Class<T> type) {
		return StoreFacade.implementation.get(name, type);
	}

}
