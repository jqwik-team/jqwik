package net.jqwik.engine.facades;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

import static net.jqwik.engine.properties.arbitraries.ArbitrariesSupport.*;

/**
 * Is loaded through reflection in api module
 */
public class ArbitraryFacadeImpl extends Arbitrary.ArbitraryFacade {

	@Override
	public <T> ListArbitrary<T> list(Arbitrary<T> elementArbitrary) {
		return new DefaultListArbitrary<>(elementArbitrary);
	}

	@Override
	public <T> SetArbitrary<T> set(Arbitrary<T> elementArbitrary) {
		// The set can never be larger than the max number of possible elements
		return new DefaultSetArbitrary<>(elementArbitrary)
					   .ofMaxSize(maxNumberOfElements(elementArbitrary, RandomGenerators.DEFAULT_COLLECTION_SIZE));
	}

	@Override
	public <T> StreamArbitrary<T> stream(Arbitrary<T> elementArbitrary) {
		return new DefaultStreamArbitrary<>(elementArbitrary);
	}

	@Override
	public <T> IteratorArbitrary<T> iterator(Arbitrary<T> elementArbitrary) {
		return new DefaultIteratorArbitrary<>(elementArbitrary);
	}

	@Override
	public <T, A> ArrayArbitrary<T, A> array(Arbitrary<T> elementArbitrary, Class<A> arrayClass) {
		return DefaultArrayArbitrary.forArrayType(elementArbitrary, arrayClass);
	}

	@Override
	public <T> Arbitrary<T> filter(Arbitrary<T> self, Predicate<T> filterPredicate, int maxMisses) {
		return new ArbitraryDelegator<T>(self) {
			@Override
			public RandomGenerator<T> generator(int genSize) {
				return super.generator(genSize).filter(filterPredicate, maxMisses);
			}

			@Override
			public RandomGenerator<T> generatorWithEmbeddedEdgeCases(int genSize) {
				return super.generatorWithEmbeddedEdgeCases(genSize).filter(filterPredicate, maxMisses);
			}

			@Override
			public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
				return super.exhaustive(maxNumberOfSamples)
							.map(generator -> generator.filter(filterPredicate, maxMisses));
			}

			@Override
			public EdgeCases<T> edgeCases(int maxEdgeCases) {
				return EdgeCasesSupport.filter(super.edgeCases(maxEdgeCases), filterPredicate);
			}
		};
	}

	@Override
	public <T, U> Arbitrary<U> map(Arbitrary<T> self, Function<T, U> mapper) {
		return new Arbitrary<U>() {
			@Override
			public RandomGenerator<U> generator(int genSize) {
				return self.generator(genSize).map(mapper);
			}

			@Override
			public RandomGenerator<U> generatorWithEmbeddedEdgeCases(int genSize) {
				return self.generatorWithEmbeddedEdgeCases(genSize).map(mapper);
			}

			@Override
			public Optional<ExhaustiveGenerator<U>> exhaustive(long maxNumberOfSamples) {
				return self.exhaustive(maxNumberOfSamples)
						   .map(generator -> generator.map(mapper));
			}

			@Override
			public EdgeCases<U> edgeCases(int maxEdgeCases) {
				return EdgeCasesSupport.map(self.edgeCases(maxEdgeCases), mapper);
			}

			@Override
			public boolean isGeneratorMemoizable() {
				return self.isGeneratorMemoizable();
			}
		};
	}

	@Override
	public <T, U> Arbitrary<U> flatMap(Arbitrary<T> self, Function<T, Arbitrary<U>> mapper) {
		return new Arbitrary<U>() {
			@Override
			public RandomGenerator<U> generator(int genSize) {
				return self.generator(genSize).flatMap(mapper, genSize, false);
			}

			@Override
			public RandomGenerator<U> generatorWithEmbeddedEdgeCases(int genSize) {
				return self.generatorWithEmbeddedEdgeCases(genSize).flatMap(mapper, genSize, true);
			}

			@Override
			public Optional<ExhaustiveGenerator<U>> exhaustive(long maxNumberOfSamples) {
				return self.exhaustive(maxNumberOfSamples)
						   .flatMap(generator -> ExhaustiveGenerators.flatMap(generator, mapper, maxNumberOfSamples));
			}

			@Override
			public EdgeCases<U> edgeCases(int maxEdgeCases) {
				return EdgeCasesSupport.flatMapArbitrary(self.edgeCases(maxEdgeCases), mapper, maxEdgeCases);
			}

			@Override
			public boolean isGeneratorMemoizable() {
				return self.isGeneratorMemoizable();
			}
		};
	}

	@Override
	public <T> Stream<T> sampleStream(Arbitrary<T> arbitrary) {
		return new SampleStreamFacade().sampleStream(arbitrary);
	}

	@Override
	public <T> Arbitrary<T> injectNull(Arbitrary<T> self, double nullProbability) {
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
	public <T> Arbitrary<T> ignoreException(Arbitrary<T> self, Class<? extends Throwable> exceptionType) {
		return new ArbitraryDelegator<T>(self) {
			@Override
			public RandomGenerator<T> generator(int genSize) {
				return super.generator(genSize).ignoreException(exceptionType);
			}

			@Override
			public RandomGenerator<T> generatorWithEmbeddedEdgeCases(int genSize) {
				return super.generatorWithEmbeddedEdgeCases(genSize).ignoreException(exceptionType);
			}

			@Override
			public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
				return super.exhaustive(maxNumberOfSamples)
							.map(generator -> generator.ignoreException(exceptionType));
			}

			@Override
			public EdgeCases<T> edgeCases(int maxEdgeCases) {
				return EdgeCasesSupport.ignoreException(self.edgeCases(maxEdgeCases), exceptionType);
			}
		};
	}

	@Override
	public <T> Arbitrary<T> dontShrink(Arbitrary<T> self) {
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
	public <T> Arbitrary<T> configureEdgeCases(Arbitrary<T> self, Consumer<EdgeCases.Config<T>> configurator) {
		return new ArbitraryDelegator<T>(self) {
			@Override
			public EdgeCases<T> edgeCases(int maxEdgeCases) {
				GenericEdgeCasesConfiguration<T> config = new GenericEdgeCasesConfiguration<>();
				return config.configure(configurator, self::edgeCases, maxEdgeCases);
			}
		};
	}

	@Override
	public <T> Arbitrary<T> withoutEdgeCases(Arbitrary<T> self) {
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
	public <T> RandomGenerator<T> memoizedGenerator(Arbitrary<T> self, int genSize, boolean withEdgeCases) {
		return Memoize.memoizedGenerator(self, genSize, withEdgeCases, () -> generator(self, genSize, withEdgeCases));
	}

	private <U> RandomGenerator<U> generator(Arbitrary<U> arbitrary, int genSize, boolean withEdgeCases) {
		if (withEdgeCases) {
			int maxEdgeCases = Math.max(genSize, 10);
			return arbitrary.generatorWithEmbeddedEdgeCases(genSize).withEdgeCases(genSize, arbitrary.edgeCases(maxEdgeCases));
		} else {
			return arbitrary.generator(genSize);
		}
	}

}
