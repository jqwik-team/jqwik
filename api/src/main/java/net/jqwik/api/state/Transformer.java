package net.jqwik.api.state;

import java.util.function.*;

import org.apiguardian.api.*;
import org.jetbrains.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * A transformer is used to transform a state of type {@code T} into another value of this type.
 * Transformation can create a new object, or change the inner state of an object and return it.
 *
 * <p>
 * In addition to performing a state transformation the mutator function can also
 * check or assert conditions and invariants that should hold when doing the transformation.
 * This is especially useful when a transformer is used as part of an {@linkplain Action action}.
 * </p>
 *
 * @param <T> The type of state to be transformed in a chain
 * @see Chain
 * @see Transformation
 * @see Action
 */
@FunctionalInterface
@API(status = EXPERIMENTAL, since = "1.7.0")
public interface Transformer<T> extends Function<@NotNull T, @NotNull T> {

	/**
	 * The singleton object used for all calls to {@linkplain #endOfChain()}.
	 */
	Transformer<?> END_OF_CHAIN = new Transformer<Object>() {
		@Override
		public @NotNull Object apply(@NotNull Object t) {
			return t;
		}

		@Override
		public String transformation() {
			return "End of Chain";
		}

		@Override
		public boolean isEndOfChain() {
			return true;
		}

		@Override
		public String toString() {
			return transformation();
		}
	};

	/**
	 * The singleton object used for all calls to {@linkplain #noop()}.
	 */
	Transformer<?> NOOP = new Transformer<Object>() {
		@Override
		public @NotNull Object apply(@NotNull Object t) {
			return t;
		}

		@Override
		public String transformation() {
			return "noop";
		}

		@Override
		public boolean isEndOfChain() {
			return false;
		}

		@Override
		public String toString() {
			return transformation();
		}
	};

	/**
	 * Use this transformer to stop further enhancement of a chain.
	 *
	 * <p>
	 * Whenever a {@linkplain Transformer#endOfChain()} is chosen by a chain,
	 * the chain ends with the current state being provided (e.g. in the chain's iterator) for a last time.
	 * </p>
	 *
	 * @param <T> The transformer's state value type
	 * @return a transformer instance
	 */
	@SuppressWarnings("unchecked")
	static <T> Transformer<T> endOfChain() {
		return (Transformer<T>) END_OF_CHAIN;
	}

	/**
	 * Use this transformer to signal a noop transformation.
	 * This can be useful when a {@linkplain Transformation} wants to provide
	 * a transformer that does nothing in some circumstances.
	 *
	 * <p>
	 * Noop transformers are ignored and not added to the chain of transformers.
	 * </p>
	 *
	 * @param <T> The transformer's state value type
	 * @return a transformer instance
	 */
	@SuppressWarnings("unchecked")
	static <T> Transformer<T> noop() {
		return (Transformer<T>) NOOP;
	}

	/**
	 * Create a transformer with a description
	 *
	 * @param description A text to describe what the transform is doing
	 * @param transform   The actual transforming function
	 * @param <S>         The type of the state to transform
	 * @return a new instance of a transformer
	 */
	static <S> Transformer<S> transform(String description, Function<S, S> transform) {
		return new Transformer<S>() {
			@Override
			public S apply(S s) {
				return transform.apply(s);
			}

			@Override
			public String transformation() {
				return description;
			}
		};
	}

	/**
	 * Convenience method to create a transformer with a description.
	 * A mutator works on a mutable, stateful object which will always be returned.
	 *
	 * @param description A text to describe what the transform is doing
	 * @param mutate      The actual mutating operation
	 * @param <S>         The type of the state to mutate
	 * @return a new instance of a transformer
	 */
	static <S> Transformer<S> mutate(String description, Consumer<S> mutate) {
		Function<S, S> transformer = s -> {
			mutate.accept(s);
			return s;
		};
		return transform(description, transformer);
	}

	/**
	 * Describe the transformation this {@linkplain Transformer} is doing in a human understandable way.
	 *
	 * @return non-empty String
	 */
	default String transformation() {
		return toString();
	}

	default boolean isEndOfChain() {
		return false;
	}
}
