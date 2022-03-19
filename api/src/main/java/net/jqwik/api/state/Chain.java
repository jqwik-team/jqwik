package net.jqwik.api.state;

import org.apiguardian.api.*;

import java.util.*;

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
 *     Chains can be generated through methods on class {@linkplain Chains}.
 * </p>
 *
 * @see Chains
 * @see Transformer
 *
 * @param <T> The type of state to be transformed in a chain
 */
@API(status = EXPERIMENTAL, since = "1.7.0")
public interface Chain<T> extends Iterable<T> {

	/**
	 * The {@linkplain Iterator} will iterate through elements representing states in order,
	 * i.e. their number is one higher than the number of transformations applied to the initial state.
	 *
	 * <p>
	 *     Mind that the next state element often depends on both randomness and the previous state.
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
	 * A list of strings that represent the transformations performed on the initial state in a textual form.
	 *
	 * @return list of strings
	 */
	List<String> transformations();

	/**
	 * The maximum number of transformations that a chain can go through.
	 *
	 * @return a number >= 1
	 */
	int maxTransformations();
}
