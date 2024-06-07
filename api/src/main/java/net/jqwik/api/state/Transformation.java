package net.jqwik.api.state;

import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * A transformation provides an arbitrary of {@linkplain Transformer transformers}
 * for values of type {@code T} in the context of {@linkplain Chain chains}.
 * The provided arbitrary of transformers can depend on the previous state,
 * which can be retrieved using the first {@linkplain Supplier supplier} argument of the function.
 * A transformation can also be restricted by a precondition,
 * which must hold for the transformation to be applicable.
 *
 * @param <T> The type of state to be transformed in a chain
 * @see Chain
 * @see Transformer
 */
@FunctionalInterface
@API(status = EXPERIMENTAL, since = "1.7.0")
public interface Transformation<T> extends Function<Supplier<T>, Arbitrary<Transformer<T>>> {

	Predicate<?> NO_PRECONDITION = ignore -> false;

	class Builder<T> {
		final private Predicate<T> precondition;

		private Builder(Predicate<T> precondition) {
			this.precondition = precondition;
		}

		public Transformation<T> provide(Arbitrary<Transformer<T>> arbitrary) {
			return new Transformation<T>() {
				@Override
				public Predicate<T> precondition() {
					return precondition;
				}

				@Override
				public Arbitrary<Transformer<T>> apply(Supplier<T> ignore) {
					return arbitrary;
				}
			};
		}

		public Transformation<T> provide(Function<T, Arbitrary<Transformer<T>>> arbitraryCreator) {
			return new Transformation<T>() {
				@Override
				public Predicate<T> precondition() {
					return precondition;
				}

				@Override
				public Arbitrary<Transformer<T>> apply(Supplier<T> supplier) {
					return arbitraryCreator.apply(supplier.get());
				}
			};
		}
	}

	/**
	 * Create a TransformerProvider with a precondition
	 */
	static <T extends @Nullable Object> Builder<T> when(Predicate<T> precondition) {
		return new Builder<T>(precondition);
	}

	/**
	 * Override this method if the applicability of the provided transformers depends on the previous state
	 *
	 * @return a predicate with input {@code T}
	 */
	@SuppressWarnings("unchecked")
	default Predicate<T> precondition() {
		return (Predicate<T>) NO_PRECONDITION;
	}
}
