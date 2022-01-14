package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;
import org.jetbrains.annotations.*;

import net.jqwik.api.Tuple.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.5.4")
public class Builders {

	private Builders() {
	}

	/**
	 * Combine Arbitraries by means of a builder.
	 *
	 * @param builderSupplier The supplier will be called freshly for each value generation.
	 *                        For exhaustive generation all supplied objects are
	 *                        supposed to be identical.
	 * @return BuilderCombinator instance
	 */
	public static <B> BuilderCombinator<B> withBuilder(Supplier<B> builderSupplier) {
		return new BuilderCombinator<>(builderSupplier, Collections.emptyList());
	}

	/**
	 * Provide access to combinators through builder functionality.
	 * <p>
	 * A builder is created through {@linkplain #withBuilder(Supplier)}.
	 *
	 * @param <B> The builder's type
	 */
	public static class BuilderCombinator<B> {
		private final Supplier<B> starter;
		private final List<Tuple3<Double, Arbitrary<Object>, BiFunction<B, Object, B>>> mutators;

		private BuilderCombinator(Supplier<B> starter, List<Tuple3<Double, Arbitrary<Object>, BiFunction<B, Object, B>>> mutators) {
			this.starter = starter;
			this.mutators = mutators;
		}

		/**
		 * Use an arbitrary of type {@code T} in this builder
		 *
		 * @param arbitrary
		 * @param <T>
		 * @return new {@linkplain CombinableBuilder} instance
		 */
		public <T> CombinableBuilder<B, T> use(Arbitrary<T> arbitrary) {
			return new CombinableBuilder<>(this, 1.0, arbitrary);
		}

		/**
		 * Create the final arbitrary.
		 *
		 * @param buildFunction Function to map a builder to an object
		 * @param <T>           the target object's type
		 * @return arbitrary of target object
		 */
		public <T> Arbitrary<T> build(Function<B, T> buildFunction) {

			class Holder {
				@Nullable final Object value;

				Holder(@Nullable Object value) {
					this.value = value;
				}
			}

			// Doing it in a single combine instead of flatMapping over all arbitraries
			// leads to better performance and forgoes some problems with stateful builders
			List<Arbitrary<Optional<Holder>>> arbitraries =
				mutators.stream()
					.map(mutator -> {
						double presenceProbability = mutator.get1();
						Arbitrary<Holder> nullable = mutator.get2().map(Holder::new);
						return nullable.optional(presenceProbability);
					})
					.collect(Collectors.toList());

			Arbitrary<B> aBuilder = Combinators.combine(arbitraries).as(values -> {
				B builder = starter.get();
				for (int i = 0; i < values.size(); i++) {
					Optional<Holder> optional = values.get(i);
					if (optional.isPresent()) {
						Object value = optional.get().value;
						BiFunction<B, Object, B> mutator = mutators.get(i).get3();
						//noinspection ConstantConditions
						builder = mutator.apply(builder, value);
					}
				}
				return builder;
			});
			return aBuilder.map(buildFunction);
		}

		/**
		 * Create the final arbitrary if it's the builder itself.
		 *
		 * @return arbitrary of builder
		 */
		public Arbitrary<B> build() {
			return build(Function.identity());
		}

		BuilderCombinator<B> withMutator(double probabilityOfUse, Arbitrary<Object> arbitrary, BiFunction<B, Object, B> mutate) {
			List<Tuple3<Double, Arbitrary<Object>, BiFunction<B, Object, B>>> newMutators = new ArrayList<>(mutators);
			newMutators.add(Tuple.of(probabilityOfUse, arbitrary, mutate));
			return new BuilderCombinator<>(starter, newMutators);
		}
	}

	/**
	 * Functionality to manipulate a builder. Instances are created through
	 * {@link BuilderCombinator#use(Arbitrary)}.
	 *
	 * @param <B> The builder's type
	 */
	public static class CombinableBuilder<B, T> {
		private final BuilderCombinator<B> combinator;
		private final double probabilityOfUse;
		private final Arbitrary<T> arbitrary;

		private CombinableBuilder(BuilderCombinator<B> combinator, double probabilityOfUse, Arbitrary<T> arbitrary) {
			this.combinator = combinator;
			this.probabilityOfUse = probabilityOfUse;
			this.arbitrary = arbitrary;
		}

		/**
		 * Set probability for using this arbitrary
		 *
		 * @param probabilityOfUse Must be between 0.0 and 1.0
		 * @return {@linkplain BuilderCombinator} instance
		 */
		public CombinableBuilder<B, T> withProbability(double probabilityOfUse) {
			if (probabilityOfUse < 0.0 || probabilityOfUse > 1.0) {
				String message = String.format("Usage probability of [%s] is outside allowed range (0;1)", probabilityOfUse);
				throw new IllegalArgumentException(message);
			}
			return new CombinableBuilder<>(combinator, probabilityOfUse, arbitrary);
		}

		/**
		 * Use the last provided arbitrary to change the builder object.
		 * Potentially create a different kind of builder.
		 *
		 * @param toFunction Use value provided by arbitrary to set current builder
		 *                   and return builder of same type.
		 * @return new {@linkplain BuilderCombinator} instance
		 */
		@SuppressWarnings("unchecked")
		public BuilderCombinator<B> in(BiFunction<B, T, B> toFunction) {
			if (probabilityOfUse == 0.0) {
				return combinator;
			}
			BiFunction<B, Object, B> mutate = (B builder, Object object) -> toFunction.apply(builder, (T) object);
			return combinator.withMutator(probabilityOfUse, arbitrary.asGeneric(), mutate);
		}

		/**
		 * Use the last provided arbitrary to change the builder object
		 * and proceed with the same builder. The most common scenario is
		 * a builder the methods of which do not return a new builder.
		 *
		 * @param setter Use value provided by arbitrary to change a builder's property.
		 * @return new {@linkplain BuilderCombinator} instance with same embedded builder
		 */
		public BuilderCombinator<B> inSetter(BiConsumer<B, T> setter) {
			BiFunction<B, T, B> toFunction = (b, t) -> {
				setter.accept(b, t);
				return b;
			};
			return in(toFunction);
		}
	}

}
