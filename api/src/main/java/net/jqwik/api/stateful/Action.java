package net.jqwik.api.stateful;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * An action class represents a state change that can be preformed
 * on a stateful {@code S}.
 *
 * At runtime the execution of an action is regulated by a precondition.
 *
 * @param <S> Type of the state object
 */
@API(status = MAINTAINED, since = "1.0")
public interface Action<S extends @Nullable Object> {

	/**
	 * If this method returns false, the action will not be performed.
	 *
	 * @param state the current state
	 * @return true if the precondition holds
	 */
	default boolean precondition(S state) {
		return true;
	}

	/**
	 * Perform an action on state {@code S} and return the same state (if it has state)
	 * or a new one representing the new state.
	 *
	 * @param state the current state
	 * @return the new state, which may or may not be the same object
	 */
	S run(S state);
}
