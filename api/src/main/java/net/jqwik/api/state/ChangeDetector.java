package net.jqwik.api.state;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * A change detector is used to determine if a stateful object has changed after the application of a transformer.
 * This can become improve shrinking of {@linkplain Chain chins} and {@linkplain ActionChain actionChains}.
 *
 * @param <T> the type of the stateful object
 *
 * @see Transformer
 * @see ChainArbitrary#improveShrinkingWith(Supplier)
 * @see ActionChainArbitrary#improveShrinkingWith(Supplier)
 */
@API(status = EXPERIMENTAL, since = "1.7.0")
public interface ChangeDetector<T> {

	/**
	 * A change detector that can be used for immutable types that implement an equals() method
	 * @param <T> the type of the stateful object
	 * @return new instance of change detector
	 */
	static <T> ChangeDetector<T> forImmutables() {
		return new ChangeDetector<T>() {
			private T before = null;

			@Override
			public void before(T before) {
				this.before = before;
			}

			@Override
			public boolean hasChanged(T after) {
				return !Objects.equals(before, after);
			}
		};
	}

	@API(status = INTERNAL)
	static <T> ChangeDetector<T> alwaysTrue() {
		return new ChangeDetector<T>() {
			@Override
			public void before(T before) {
			}

			@Override
			public boolean hasChanged(T after) {
				return true;
			}
		};
	}

	/**
	 * Get and remember the state before it is handed to a {@linkplain Transformer transformer}.
	 */
	void before(T before);

	/**
	 * Determine if the state object has changed.
	 *
	 * @param after The state resulting from handing it to a {@linkplain Transformer transformer}.
	 */
	boolean hasChanged(T after);
}
