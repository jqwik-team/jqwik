package net.jqwik.api.state;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

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
public interface ChangeDetector<T extends @Nullable Object> {

	/**
	 * A change detector that can be used for immutable types that implement an equals() method
	 * @param <T> the type of the stateful object
	 * @return new instance of change detector
	 */
	static <T extends @Nullable Object> ChangeDetector<T> forImmutables() {
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
	static ChangeDetector<@Nullable Object> alwaysTrue() {
		return new ChangeDetector<@Nullable Object>() {
			@Override
			public void before(@Nullable Object before) {
			}

			@Override
			public boolean hasChanged(@Nullable Object after) {
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
