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
		return new DefaultListArbitrary<>(elementArbitrary, elementArbitrary.isUnique());
	}

	@Override
	public <T> SetArbitrary<T> set(Arbitrary<T> elementArbitrary) {
		// The set can never be larger than the max number of possible elements
		return new DefaultSetArbitrary<>(elementArbitrary)
					   .ofMaxSize(maxNumberOfElements(elementArbitrary, RandomGenerators.DEFAULT_COLLECTION_SIZE));
	}

	@Override
	public <T> StreamArbitrary<T> stream(Arbitrary<T> elementArbitrary) {
		return new DefaultStreamArbitrary<>(elementArbitrary, elementArbitrary.isUnique());
	}

	@Override
	public <T> IteratorArbitrary<T> iterator(Arbitrary<T> elementArbitrary) {
		return new DefaultIteratorArbitrary<>(elementArbitrary, elementArbitrary.isUnique());
	}

	@Override
	public <T, A> ArrayArbitrary<T, A> array(Arbitrary<T> elementArbitrary, Class<A> arrayClass) {
		return new DefaultArrayArbitrary<>(elementArbitrary, arrayClass, elementArbitrary.isUnique());
	}

	@Override
	public <T> Arbitrary<T> filter(Arbitrary<T> self, Predicate<T> filterPredicate) {
		return new ArbitraryDelegator<T>(self) {
			@Override
			public RandomGenerator<T> generator(int genSize) {
				return super.generator(genSize).filter(filterPredicate);
			}

			@Override
			public RandomGenerator<T> generatorWithEmbeddedEdgeCases(int genSize) {
				return super.generatorWithEmbeddedEdgeCases(genSize).filter(filterPredicate);
			}

			@Override
			public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
				return super.exhaustive(maxNumberOfSamples)
						   .map(generator -> generator.filter(filterPredicate));
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
				return self.generator(genSize, true).map(mapper);
			}

			@Override
			public boolean isUnique() {
				return self.isUnique();
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
				return self.generator(genSize, true).flatMap(mapper, genSize, true);
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
		};
	}

	@Override
	public <T> Stream<T> sampleStream(Arbitrary<T> arbitrary) {
		return arbitrary.generator(JqwikProperties.DEFAULT_TRIES, true)
						.stream(SourceOfRandomness.current())
						.map(Shrinkable::value);
	}

	@Override
	public <T> Arbitrary<T> injectNull(Arbitrary<T> self, double nullProbability) {
		int frequencyNull = (int) Math.round(nullProbability * 100);
		int frequencyNotNull = 100 - frequencyNull;
		if (frequencyNull <= 0) {
			return self;
		}
		if (frequencyNull >= 100) {
			return Arbitraries.just(null);
		}
		Arbitrary<T> withNull = Arbitraries.frequencyOf(
				Tuple.of(frequencyNull, Arbitraries.just(null)),
				Tuple.of(frequencyNotNull, self)
		);
		if (self.isUnique()) {
			return withNull.unique();
		} else {
			return withNull;
		}
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
				return config.configure(configurator, self.edgeCases(maxEdgeCases));
			}
		};
	}

	@Override
	public <T> Arbitrary<T> withoutEdgeCases(Arbitrary<T> self) {
		return new ArbitraryDelegator<T>(self) {
			@Override
			public RandomGenerator<T> generator(int genSize, boolean withEdgeCases) {
				return self.generator(genSize);
			}

			@Override
			public EdgeCases<T> edgeCases(int maxEdgeCases) {
				return EdgeCases.none();
			}
		};
	}
}
