package net.jqwik.api.stateful;

import org.apiguardian.api.*;

import net.jqwik.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.0")
public interface ActionSequenceArbitrary<M> extends SizableArbitrary<ActionSequence<M>> {

	ActionSequenceArbitrary<M> ofMinSize(int minSize);

	ActionSequenceArbitrary<M> ofMaxSize(int maxSize);

	default ActionSequenceArbitrary<M> ofSize(int size) {
		return ofMinSize(size).ofMaxSize(size);
	}

}
