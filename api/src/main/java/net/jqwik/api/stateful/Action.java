package net.jqwik.api.stateful;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * An action class represents a state change that can be preformed
 * on a stateful {@code S}.
 *
 * At runtime the execution of an action is regulated by a precondition.
 *
 * @param <S>
 */
@API(status = MAINTAINED, since = "1.0")
public interface Action<S> {

	/**
	 * If this method returns false, the action will not be performed.
	 *
	 * @param state
	 * @return
	 */
	default boolean precondition(S state) {
		return true;
	}

	/**
	 * Perform an action on state {@code S} and return the same state (if it has state)
	 * or a new one representing the new state.
	 *
	 * @param state
	 * @return
	 */
	S run(S state);
}
