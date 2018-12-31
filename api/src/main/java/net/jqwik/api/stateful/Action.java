package net.jqwik.api.stateful;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * An action class represents a state change that can be preformed on a model {@code M}.
 *
 * At runtime the execution of an action is regulated by a precondition.
 *
 * @param <M>
 */
@API(status = MAINTAINED, since = "1.0")
public interface Action<M> {

	/**
	 * If this method returns false, the action will not be performed.
	 *
	 * @param model
	 * @return
	 */
	default boolean precondition(M model) {
		return true;
	}

	/**
	 * Perform an action on model {@code M} and return the same model (if it has state)
	 * or a new one representing the new state.
	 *
	 * @param model
	 * @return
	 */
	M run(M model);
}
