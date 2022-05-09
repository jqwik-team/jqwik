package net.jqwik.api.stateful;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.0")
public interface ActionSequenceArbitrary<M> extends Arbitrary<ActionSequence<M>> {

	/**
	 * Set the intended number of steps of this sequence.
	 */
	ActionSequenceArbitrary<M> ofSize(int size);

}
