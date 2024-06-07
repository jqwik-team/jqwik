package net.jqwik.api.state;

import org.apiguardian.api.*;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * A chain represents a series of states of type {@code T} in which the previous state
 * is somehow transformed into the next state. {@linkplain Transformer Transformers} are
 * used to transform a state into the next state.
 * The term chain is used in relation to mathematical concepts like Markov chains.
 *
 * <p>
 *     State instances can be mutable or immutable.
 * </p>
 *
 * <p>
 *     Chains can be generated through {@linkplain Chain#startWith(Supplier)}.
 * </p>
 *
 * @see Transformer
 *
 * @param <T> The type of state to be transformed in a chain
 */
@API(status = EXPERIMENTAL, since = "1.7.0")
public interface Chain<T extends @Nullable Object> extends Iterable<T> {

	@API(status = INTERNAL)
	abstract class ChainFacade {
		private static final ChainFacade implementation;

		static {
			implementation = FacadeLoader.load(ChainFacade.class);
		}

		public abstract <T extends @Nullable Object> ChainArbitrary<T> startWith(Supplier<? extends T> initialSupplier);
	}

	/**
	 * Create arbitrary for {@linkplain Chain chains} with a certain initial state.
	 *
	 * @param initialSupplier function to create the initial state object
	 * @param <T>             The type of state to be transformed through the chain.
	 * @return new arbitrary instance
	 */
	static <T extends @Nullable Object> ChainArbitrary<T> startWith(Supplier<? extends T> initialSupplier) {
		return ChainFacade.implementation.startWith(initialSupplier);
	}

	/**
	 * The {@linkplain Iterator} will iterate through elements representing states in order,
	 * i.e. their number is one higher than the number of transformations applied to the initial state.
	 *
	 * <p>
	 *     Mind that the next state element can depend on both the previous state and (pseudo-)randomness.
	 *     Several iterators must always produce the same "chain" of states.
	 *     Each iterator will start with a new instance of the initial state.
	 * </p>
	 *
	 * @return an iterator through all states
	 */
	Iterator<T> start();

	@Override
	default Iterator<T> iterator() {
		return start();
	}

	/**
	 * Return list of all applied transformations as far as iterators have been used.
	 *
	 * <p>
	 *     For a chain that has not been iterated through this list is empty.
	 * </p>
	 *
	 * @return list of describing strings
	 */
	List<String> transformations();

	/**
	 * Return list of all used transformer instances.
	 *
	 * <p>
	 * Checking transformer instances - e.g. if they are of a certain implementation type -
	 * only makes sense if the transformer's description string is NOT set explicitly.
	 * </p>
	 *
	 * <p>
	 * For a chain that has not been run this list is always empty.
	 * </p>
	 *
	 * @return list of transformer instances
	 */
	@API(status = EXPERIMENTAL, since = "1.7.1")
	List<Transformer<T>> transformers();

	/**
	 * The maximum number of transformations that a chain can go through.
	 *
	 * @return a number &gt;= 1
	 */
	int maxTransformations();
}
