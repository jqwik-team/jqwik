package net.jqwik.api.stateful;

import net.jqwik.api.*;

public interface ActionSequenceArbitrary<M> extends Arbitrary<ActionSequence<M>> {

	/**
	 * Set the number of actions in a generated sequence to {@code size}.
	 */
	ActionSequenceArbitrary<M> ofSize(int size);
}
