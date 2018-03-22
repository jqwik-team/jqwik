package net.jqwik.api.stateful;

import net.jqwik.api.*;

public interface ActionSequenceArbitrary<M> extends Arbitrary<ActionSequence<M>> {

	ActionSequenceArbitrary<M> ofSize(int size);
}
