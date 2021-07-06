package net.jqwik.api.stateful;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.0")
public interface ActionSequenceArbitrary<M> extends Arbitrary<ActionSequence<M>> {

	/**
	 * This method has no effect. Don't use it.
	 *
	 * @deprecated Just remove this call. Will be removed in 1.7.0
	 */
	@Deprecated
	@API(status = DEPRECATED, since = "1.5.3")
	ActionSequenceArbitrary<M> ofMinSize(int minSize);

	/**
	 * This method is equivalent to {@linkplain #ofSize(int)}
	 *
	 * @deprecated Use {@linkplain #ofSize(int)} instead. Will be removed in 1.7.0.
	 */
	@API(status = DEPRECATED, since = "1.5.3")
	default ActionSequenceArbitrary<M> ofMaxSize(int maxSize) {
		return ofSize(maxSize);
	}

	/**
	 * Set the intended number of steps of this sequence.
	 */
	ActionSequenceArbitrary<M> ofSize(int size);

}
