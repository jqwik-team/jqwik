package net.jqwik.api.stateful;

import net.jqwik.api.arbitraries.*;

public interface ActionSequenceArbitrary<M> extends SizableArbitrary<ActionSequence<M>> {

	ActionSequenceArbitrary<M> ofMinSize(int minSize);

	ActionSequenceArbitrary<M> ofMaxSize(int maxSize);

	default ActionSequenceArbitrary<M> ofSize(int size) {
		return ofMinSize(size).ofMaxSize(size);
	}

}
