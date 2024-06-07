package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

import org.jspecify.annotations.*;

import static net.jqwik.engine.properties.arbitraries.ArbitrariesSupport.*;

/**
 * Is loaded through reflection in api module
 */
public class ArbitraryFacadeImpl extends Arbitrary.ArbitraryFacade {

	@Override
	public <T extends @Nullable Object> ListArbitrary<T> list(Arbitrary<T> elementArbitrary) {
		return new DefaultListArbitrary<>(elementArbitrary);
	}

	@Override
	public <T extends @Nullable Object> SetArbitrary<T> set(Arbitrary<T> elementArbitrary) {
		// The set can never be larger than the max number of possible elements
		return new DefaultSetArbitrary<>(elementArbitrary)
				   .ofMaxSize(maxNumberOfElements(elementArbitrary, RandomGenerators.DEFAULT_COLLECTION_SIZE));
	}

	@Override
	public <T extends @Nullable Object> StreamArbitrary<T> stream(Arbitrary<T> elementArbitrary) {
		return new DefaultStreamArbitrary<>(elementArbitrary);
	}

	@Override
	public <T extends @Nullable Object> IteratorArbitrary<T> iterator(Arbitrary<T> elementArbitrary) {
		return new DefaultIteratorArbitrary<>(elementArbitrary);
	}

	@Override
	public <T extends @Nullable Object, A> ArrayArbitrary<T, A> array(Arbitrary<T> elementArbitrary, Class<A> arrayClass) {
		return DefaultArrayArbitrary.forArrayType(elementArbitrary, arrayClass);
	}

	@Override
	public <T extends @Nullable Object> Arbitrary<T> filter(Arbitrary<T> self, Predicate<? super T> filterPredicate, int maxMisses) {
		return new ArbitraryFilter<>(self, filterPredicate, maxMisses);
	}

	@Override
	public <T extends @Nullable Object, U extends @Nullable Object> Arbitrary<U> map(Arbitrary<T> self, Function<? super T, ? extends U> mapper) {
		return new ArbitraryMap<>(self, mapper);
	}

	@Override
	public <T extends @Nullable Object, U extends @Nullable Object> Arbitrary<U> flatMap(Arbitrary<T> self, Function<? super T, ? extends Arbitrary<U>> mapper) {
		return new ArbitraryFlatMap<>(self, mapper);
	}

	@Override
	public <T extends @Nullable Object> Stream<T> sampleStream(Arbitrary<T> arbitrary) {
		return new SampleStreamFacade().sampleStream(arbitrary);
	}

	@Override
	public <T extends @Nullable Object> Arbitrary<@Nullable T> injectNull(Arbitrary<T> self, double nullProbability) {
		int frequencyNull = (int) Math.round(nullProbability * 1000);
		int frequencyNotNull = 1000 - frequencyNull;
		if (frequencyNull <= 0) {
			return self;
		}
		if (frequencyNull >= 1000) {
			return Arbitraries.just(null);
		}
		return Arbitraries.frequencyOf(
			Tuple.of(frequencyNull, Arbitraries.just(null)),
			Tuple.of(frequencyNotNull, self)
		);
	}

	@Override
	public <T extends @Nullable Object> Arbitrary<T> ignoreExceptions(Arbitrary<T> self, int maxThrows, Class<? extends Throwable>[] exceptionTypes) {
		if (exceptionTypes.length == 0) {
			return self;
		}
		return new ArbitraryDelegator<T>(self) {
			@Override
			public RandomGenerator<T> generator(int genSize) {
				return super.generator(genSize).ignoreExceptions(maxThrows, exceptionTypes);
			}

			@Override
			public RandomGenerator<T> generatorWithEmbeddedEdgeCases(int genSize) {
				return super.generatorWithEmbeddedEdgeCases(genSize).ignoreExceptions(maxThrows, exceptionTypes);
			}

			@Override
			public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
				return super.exhaustive(maxNumberOfSamples)
							.map(generator -> generator.ignoreExceptions(maxThrows, exceptionTypes));
			}

			@Override
			public EdgeCases<T> edgeCases(int maxEdgeCases) {
				return EdgeCasesSupport.ignoreExceptions(self.edgeCases(maxEdgeCases), exceptionTypes);
			}
		};
	}

	@Override
	public <T extends @Nullable Object> Arbitrary<T> dontShrink(Arbitrary<T> self) {
		return new ArbitraryDelegator<T>(self) {
			@Override
			public RandomGenerator<T> generator(int genSize) {
				return super.generator(genSize).dontShrink();
			}

			@Override
			public RandomGenerator<T> generatorWithEmbeddedEdgeCases(int genSize) {
				return super.generatorWithEmbeddedEdgeCases(genSize).dontShrink();
			}

			@Override
			public EdgeCases<T> edgeCases(int maxEdgeCases) {
				return EdgeCasesSupport.dontShrink(super.edgeCases(maxEdgeCases));
			}
		};
	}

	@Override
	public <T extends @Nullable Object> Arbitrary<T> configureEdgeCases(Arbitrary<T> self, Consumer<? super EdgeCases.Config<T>> configurator) {
		return new ArbitraryDelegator<T>(self) {
			@Override
			public EdgeCases<T> edgeCases(int maxEdgeCases) {
				GenericEdgeCasesConfiguration<T> config = new GenericEdgeCasesConfiguration<>();
				return config.configure(configurator, self::edgeCases, maxEdgeCases);
			}
		};
	}

	@Override
	public <T extends @Nullable Object> Arbitrary<T> withoutEdgeCases(Arbitrary<T> self) {
		return new ArbitraryDelegator<T>(self) {
			@Override
			public RandomGenerator<T> generator(int genSize, boolean withEdgeCases) {
				return Memoize.memoizedGenerator(self, genSize, withEdgeCases, () -> self.generator(genSize));
			}

			@Override
			public EdgeCases<T> edgeCases(int maxEdgeCases) {
				return EdgeCases.none();
			}

			@Override
			public RandomGenerator<T> generatorWithEmbeddedEdgeCases(int genSize) {
				return self.generator(genSize, false);
			}
		};
	}

	@Override
	public <T extends @Nullable Object> Arbitrary<T> fixGenSize(Arbitrary<T> self, int genSize) {
		return new ArbitraryDelegator<T>(self) {
			@Override
			public RandomGenerator<T> generator(int ignoredGenSize) {
				return super.generator(genSize);
			}

			@Override
			public RandomGenerator<T> generatorWithEmbeddedEdgeCases(int ignoredGenSize) {
				return super.generatorWithEmbeddedEdgeCases(genSize);
			}
		};
	}

	@Override
	public <T extends @Nullable Object> Arbitrary<List<T>> collect(Arbitrary<T> self, Predicate<? super List<? extends T>> until) {
		return new ArbitraryCollect<>(self, until);
	}

	@Override
	public <T extends @Nullable Object> RandomGenerator<T> memoizedGenerator(Arbitrary<T> self, int genSize, boolean withEdgeCases) {
		return Memoize.memoizedGenerator(self, genSize, withEdgeCases, () -> generator(self, genSize, withEdgeCases));
	}

	private <U extends @Nullable Object> RandomGenerator<U> generator(Arbitrary<U> arbitrary, int genSize, boolean withEdgeCases) {
		if (withEdgeCases) {
			int maxEdgeCases = Math.max(genSize, 10);
			return arbitrary.generatorWithEmbeddedEdgeCases(genSize).withEdgeCases(genSize, arbitrary.edgeCases(maxEdgeCases));
		} else {
			return arbitrary.generator(genSize);
		}
	}

}
