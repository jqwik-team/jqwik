package net.jqwik.engine.properties;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import org.opentest4j.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ShrinkingMode.*;
import static net.jqwik.engine.properties.PropertyConfigurationBuilder.*;

@Group
@SuppressWarnings("unchecked")
class GenericPropertyTests {

	private final Supplier<TryLifecycleContext> tryLifecycleContextSupplier = TestHelper.tryLifecycleContextSupplier();

	@Group
	class OneParameter {

		private final Function<List<Object>, Boolean> exactlyOneInteger = args -> args.size() == 1 && args.get(0) instanceof Integer;

		@Example
		void satisfied() {
			ForAllSpy forAllFunction = new ForAllSpy(trie -> true, exactlyOneInteger);

			Arbitrary<Object> arbitrary = Arbitraries.of(1, 2, 3, 4, 5);
			ParametersGenerator shrinkablesGenerator = randomizedShrinkablesGenerator(arbitrary);

			PropertyConfiguration configuration = aConfig().withTries(2).build();
			GenericProperty property = new GenericProperty("satisfied property", configuration, shrinkablesGenerator, forAllFunction, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(forAllFunction.countCalls()).isEqualTo(2);

			assertThat(result.propertyName()).isEqualTo("satisfied property");
			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.SUCCESSFUL);
			assertThat(result.countTries()).isEqualTo(2);
			assertThat(result.countChecks()).isEqualTo(2);

			assertThat(result.falsifiedParameters()).isEmpty();
			assertThat(result.shrunkSample()).isEmpty();
			assertThat(result.originalSample()).isEmpty();
		}

		@Example
		void stopWhenNoMoreShrinkablesCanBeGenerated() {
			ForAllSpy forAllFunction = new ForAllSpy(trie -> true, exactlyOneInteger);

			ParametersGenerator shrinkablesGenerator = finiteShrinkablesGenerator(1, 2, 3);

			GenericProperty property =
				new GenericProperty("finite property", aConfig()
					.build(), shrinkablesGenerator, forAllFunction, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(forAllFunction.countCalls()).isEqualTo(3);

			assertThat(result.propertyName()).isEqualTo("finite property");
			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.SUCCESSFUL);
			assertThat(result.countTries()).isEqualTo(3);
			assertThat(result.countChecks()).isEqualTo(3);
		}

		@Example
		void falsifiedThroughReturningFalseInCheckedFunction() {
			int failingTry = 5;

			ForAllSpy forAllFunction = new ForAllSpy(trie -> trie != failingTry, exactlyOneInteger);

			Arbitrary<Object> arbitrary = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5, 6, 7, 8, 9);
			ParametersGenerator shrinkablesGenerator = randomizedShrinkablesGenerator(arbitrary);

			GenericProperty property =
				new GenericProperty(
					"falsified property",
					aConfig().withShrinking(OFF).build(),
					shrinkablesGenerator,
					forAllFunction,
					tryLifecycleContextSupplier
				);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(forAllFunction.countCalls()).isEqualTo(5);

			assertThat(result.propertyName()).isEqualTo("falsified property");
			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.FAILED);
			assertThat(result.countTries()).isEqualTo(failingTry);
			assertThat(result.countChecks()).isEqualTo(failingTry);
			assertThat(result.throwable()).isPresent();

			assertThat(result.falsifiedParameters()).isPresent();
			assertThat(result.falsifiedParameters().get()).containsExactly(failingTry);

			assertThat(result.originalSample()).isPresent();
			assertThat(result.shrunkSample()).isEmpty();
		}

		@Example
		void dontShrinkIfShrinkingModeIsOff() {
			int failingTry = 5;

			ForAllSpy forAllFunction = new ForAllSpy(trie -> trie < failingTry, exactlyOneInteger);

			Arbitrary<Object> arbitrary = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5, 6, 7, 8);
			ParametersGenerator shrinkablesGenerator = randomizedShrinkablesGenerator(arbitrary);

			PropertyConfiguration configuration = aConfig().withShrinking(OFF).build();
			GenericProperty property =
				new GenericProperty("falsified property", configuration, shrinkablesGenerator, forAllFunction, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(forAllFunction.countCalls()).isEqualTo(failingTry); // If shrunk number would be higher
			assertThat(result.falsifiedParameters().get()).containsExactly(failingTry);

			assertThat(result.originalSample()).isPresent();
			assertThat(result.shrunkSample()).isEmpty();
		}

		@Example
		void dontShrinkIfFalsifiersThrowsThrowableThatIsNotAnException() {
			Function<Integer, Boolean> throwErrorOnFifthAttempt = count -> {
				if (count == 5) throw new Error();
				return true;
			};
			ForAllSpy forAllSpy = new ForAllSpy(throwErrorOnFifthAttempt);

			Arbitrary<Object> arbitrary = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5, 6, 7, 8);
			ParametersGenerator shrinkablesGenerator = randomizedShrinkablesGenerator(arbitrary);

			PropertyConfiguration configuration = aConfig().build();
			GenericProperty property =
				new GenericProperty("falsified property", configuration, shrinkablesGenerator, forAllSpy, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(forAllSpy.countCalls()).isEqualTo(5);
			assertThat(result.falsifiedParameters()).isPresent();
			assertThat(result.falsifiedParameters().get()).containsExactly(5);

			assertThat(result.shrunkSample()).isNotPresent();
			assertThat(result.originalSample().get().parameters()).containsExactly(5);
		}

		@Example
		void falsifiedThroughAssertionError() {
			AssertionError assertionError = new AssertionError("test");
			ForAllSpy forAllFunction = new ForAllSpy(trie -> {
				throw assertionError;
			}, exactlyOneInteger);

			Arbitrary<Object> arbitrary = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5);
			ParametersGenerator shrinkablesGenerator = randomizedShrinkablesGenerator(arbitrary);

			PropertyConfiguration configuration = aConfig().build();
			GenericProperty property =
				new GenericProperty("falsified property", configuration, shrinkablesGenerator, forAllFunction, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(forAllFunction.countCalls()).isEqualTo(1);

			assertThat(result.propertyName()).isEqualTo("falsified property");
			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.FAILED);
			assertThat(result.countTries()).isEqualTo(1);
			assertThat(result.countChecks()).isEqualTo(1);

			assertThat(result.falsifiedParameters()).isPresent();
			assertThat(result.falsifiedParameters().get()).containsExactly(1);

			assertThat(result.throwable()).isPresent();
			assertThat(result.throwable().get()).isSameAs(assertionError);

			assertThat(result.originalSample().get().falsifyingError()).isPresent();
			assertThat(result.originalSample().get().falsifyingError().get()).isSameAs(assertionError);
		}

		@Example
		void falsifiedThroughRuntimeException() {
			RuntimeException runtimeException = new RuntimeException("test");
			ForAllSpy forAllFunction = new ForAllSpy(trie -> {
				throw runtimeException;
			}, exactlyOneInteger);

			Arbitrary<Object> arbitrary = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5);
			ParametersGenerator shrinkablesGenerator = randomizedShrinkablesGenerator(arbitrary);

			PropertyConfiguration configuration = aConfig().build();
			GenericProperty property =
				new GenericProperty("erroneous property", configuration, shrinkablesGenerator, forAllFunction, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(forAllFunction.countCalls()).isEqualTo(1);

			assertThat(result.propertyName()).isEqualTo("erroneous property");
			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.FAILED);
			assertThat(result.countTries()).isEqualTo(1);
			assertThat(result.countChecks()).isEqualTo(1);

			assertThat(result.falsifiedParameters()).isPresent();
			assertThat(result.falsifiedParameters().get()).containsExactly(1);

			assertThat(result.throwable()).isPresent();
			assertThat(result.throwable().get()).isSameAs(runtimeException);

			assertThat(result.originalSample()).isPresent();
			assertThat(result.originalSample().get().falsifyingError().get()).isSameAs(runtimeException);
		}

		@Example
		void satisfiedWithRejectedAssumptions() {
			IntPredicate isEven = aNumber -> aNumber % 2 == 0;

			ForAllSpy forAllFunction = new ForAllSpy(aTry -> {
				Assume.that(isEven.test(aTry));
				assertThat(isEven.test(aTry)).isTrue();
				return true;
			}, exactlyOneInteger);

			Arbitrary<Object> arbitrary = Arbitraries.of(1, 2, 3, 4, 5);
			ParametersGenerator shrinkablesGenerator = randomizedShrinkablesGenerator(arbitrary);

			PropertyConfiguration configuration = aConfig().withTries(10).build();
			GenericProperty property =
				new GenericProperty("satisfied property", configuration, shrinkablesGenerator, forAllFunction, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(forAllFunction.countCalls()).isEqualTo(10);

			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.SUCCESSFUL);
			assertThat(result.countTries()).isEqualTo(10);
			assertThat(result.countChecks()).isEqualTo(5);
		}

		@Example
		void exhaustedWithAllTriesDiscarded() {
			ForAllSpy forAllFunction = new ForAllSpy(aTry -> {
				Assume.that(false);
				return true;
			}, exactlyOneInteger);

			Arbitrary<Object> arbitrary = Arbitraries.of(1, 2, 3, 4, 5);
			ParametersGenerator shrinkablesGenerator = randomizedShrinkablesGenerator(arbitrary);

			PropertyConfiguration configuration = aConfig().withTries(10).build();
			GenericProperty property =
				new GenericProperty("exhausted property", configuration, shrinkablesGenerator, forAllFunction, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(forAllFunction.countCalls()).isEqualTo(10);

			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.EXHAUSTED);
			assertThat(result.countTries()).isEqualTo(10);
			assertThat(result.countChecks()).isEqualTo(0);
			assertThat(result.throwable()).isPresent();
			assertThat(result.throwable().get()).isInstanceOf(AssertionFailedError.class);
			assertThat(result.falsifiedParameters()).isNotPresent();
		}

		@Example
		void exhaustedWithMaxDiscardRatioExceeded() {
			int maxDiscardRatio = 2; // Max 2 discards per 1 non-discard
			final AtomicInteger counter = new AtomicInteger(0);
			ForAllSpy forAllFunction = new ForAllSpy(aTry -> {
				if (counter.incrementAndGet() % 4 != 0) // 3 of 4 are discarded
					Assume.that(false);
				return true;
			}, exactlyOneInteger);

			Arbitrary<Object> arbitrary = Arbitraries.of(1, 2, 3, 4, 5);
			ParametersGenerator shrinkablesGenerator = randomizedShrinkablesGenerator(arbitrary);

			PropertyConfiguration configuration = aConfig().withTries(20).withMaxDiscardRatio(maxDiscardRatio).build();
			GenericProperty property =
				new GenericProperty("exhausted property", configuration, shrinkablesGenerator, forAllFunction, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.EXHAUSTED);
			assertThat(result.countTries()).isEqualTo(20);
			assertThat(result.countChecks()).isEqualTo(5);
		}

		@Example
		void exceptionInForAllFunctionMakesPropertyFalsified() {
			int erroneousTry = 5;
			RuntimeException thrownException = new RuntimeException("thrown in test");

			ForAllSpy forAllFunction = new ForAllSpy(aTry -> {
				if (aTry == erroneousTry) throw thrownException;
				return true;
			}, exactlyOneInteger);

			Arbitrary<Object> arbitrary = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5);
			ParametersGenerator shrinkablesGenerator = randomizedShrinkablesGenerator(arbitrary);

			PropertyConfiguration configuration = aConfig().build();
			GenericProperty property =
				new GenericProperty("erroneous property", configuration, shrinkablesGenerator, forAllFunction, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.FAILED);
			assertThat(result.countTries()).isEqualTo(erroneousTry);
			assertThat(result.countChecks()).isEqualTo(erroneousTry);

			assertThat(result.throwable()).isPresent();
			assertThat(result.throwable().get()).isSameAs(thrownException);

			assertThat(result.falsifiedParameters()).isPresent();
			assertThat(result.falsifiedParameters().get()).containsExactly(erroneousTry);
		}

		@Example
		void falsifiedAndShrunk() {
			Arbitrary<Object> arbitrary = Arbitraries.integers().between(1, 100).asGeneric();
			CheckedFunction checkedFunction = params -> ((int) params.get(0)) < 5;
			ParametersGenerator shrinkablesGenerator = randomizedShrinkablesGenerator(arbitrary);

			PropertyConfiguration configuration = aConfig().build();
			GenericProperty property =
				new GenericProperty("falsified property", configuration, shrinkablesGenerator, checkedFunction, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(result.propertyName()).isEqualTo("falsified property");
			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.FAILED);

			assertThat(result.falsifiedParameters()).isPresent();
			assertThat(result.falsifiedParameters().get()).containsExactly(5);

			assertThat(result.originalSample()).isPresent();
			// If 5 is generated directly no shrinking has taken place
			if (result.shrunkSample().isPresent()) {
				assertThat(result.shrunkSample().get().countShrinkingSteps()).isGreaterThan(0);
			}
		}

	}

	@Group
	class NoParameter {
		@Example
		void checkPropertyWithoutForAllParametersAreAlsoTriedSeveralTimes() {
			CheckedFunction forAllFunction = args -> {
				assertThat(args).isEmpty();
				return true;
			};

			PropertyConfiguration configuration = aConfig().withTries(2).build();
			GenericProperty property =
				new GenericProperty("satisfied property", configuration, emptyShrinkablesGenerator(), forAllFunction, tryLifecycleContextSupplier);

			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(result.propertyName()).isEqualTo("satisfied property");
			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.SUCCESSFUL);
			assertThat(result.countTries()).isEqualTo(2);
			assertThat(result.countChecks()).isEqualTo(2);
		}

		@Example
		void evenIfItFails() {
			CheckedFunction forAllFunction = args -> {
				assertThat(args).isEmpty();
				return false;
			};

			PropertyConfiguration configuration = aConfig().build();
			GenericProperty property =
				new GenericProperty("failing property", configuration, emptyShrinkablesGenerator(), forAllFunction, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(result.propertyName()).isEqualTo("failing property");
			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.FAILED);
			assertThat(result.countTries()).isEqualTo(1);
			assertThat(result.countChecks()).isEqualTo(1);
			assertThat(result.throwable()).isPresent();

			assertThat(result.falsifiedParameters()).isPresent();
			assertThat(result.falsifiedParameters().get()).isEmpty();
		}

		@Example
		void evenIfItThrowsException() {
			CheckedFunction forAllFunction = args -> {
				assertThat(args).isEmpty();
				throw new RuntimeException();
			};

			PropertyConfiguration configuration = aConfig().build();
			GenericProperty property =
				new GenericProperty("failing property", configuration, emptyShrinkablesGenerator(), forAllFunction, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(result.propertyName()).isEqualTo("failing property");
			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.FAILED);
			assertThat(result.countTries()).isEqualTo(1);
			assertThat(result.countChecks()).isEqualTo(1);

			assertThat(result.throwable()).isPresent();
			assertThat(result.throwable().get()).isInstanceOf(RuntimeException.class);

			assertThat(result.falsifiedParameters()).isPresent();
			assertThat(result.falsifiedParameters().get()).isEmpty();
		}

		@Example
		void abortedWhenExampleThrowsAbortedException() {
			CheckedFunction forAllFunction = args -> {
				throw new TestAbortedException("skip example");
			};

			PropertyConfiguration configuration = aConfig().withTries(1).build();
			GenericProperty property =
				new GenericProperty("skipped example", configuration, emptyShrinkablesGenerator(), forAllFunction, tryLifecycleContextSupplier);

			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(result.propertyName()).isEqualTo("skipped example");
			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.ABORTED);
			assertThat(result.throwable()).isPresent();
			assertThat(result.throwable().get()).isInstanceOf(TestAbortedException.class);
			assertThat(result.throwable().get().getMessage()).isEqualTo("skip example");
			assertThat(result.countTries()).isEqualTo(1);
			assertThat(result.countChecks()).isEqualTo(0);
		}

	}

	@Group
	class ManyParameters {

		@Example
		void twoParametersSatisfied() {
			CheckedFunction forAllFunction = args -> {
				assertThat(args).size().isEqualTo(2);
				assertThat(args.get(0)).isInstanceOf(Integer.class);
				assertThat(args.get(1)).isInstanceOf(Integer.class);
				return true;
			};

			Arbitrary<Object> arbitrary1 = Arbitraries.of(1, 2, 3, 4, 5);
			Arbitrary<Object> arbitrary2 = Arbitraries.of(1, 2, 3, 4, 5);
			ParametersGenerator shrinkablesGenerator = randomizedShrinkablesGenerator(arbitrary1, arbitrary2);

			PropertyConfiguration configuration = aConfig()
				.withSeed("1000")
				.withTries(5).build();
			GenericProperty property =
				new GenericProperty("property with 2", configuration, shrinkablesGenerator, forAllFunction, tryLifecycleContextSupplier);
			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(result.propertyName()).isEqualTo("property with 2");
			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.SUCCESSFUL);
			assertThat(result.countTries()).isEqualTo(5);
			assertThat(result.countChecks()).isEqualTo(5);
			assertThat(result.seed()).isEqualTo(Optional.of("1000"));
			assertThat(result.throwable()).isNotPresent();
			assertThat(result.falsifiedParameters()).isNotPresent();
		}

		@Example
		void fourParametersFalsified() {
			int failingTry = 5;

			CheckedFunction forAllFunction = args -> {
				assertThat(args).size().isEqualTo(4);
				return ((int) args.get(0)) < failingTry;
			};

			Arbitrary<Object> arbitrary1 = new OrderedArbitraryForTesting<>(1, 2, 3, 4, 5);
			Arbitrary<Object> arbitrary2 = Arbitraries.of(1, 2, 3, 4, 5);
			Arbitrary<Object> arbitrary3 = Arbitraries.of(1, 2, 3, 4, 5);
			Arbitrary<Object> arbitrary4 = Arbitraries.of(1, 2, 3, 4, 5);
			ParametersGenerator shrinkablesGenerator = randomizedShrinkablesGenerator(arbitrary1, arbitrary2, arbitrary3, arbitrary4);

			PropertyConfiguration configuration = aConfig().build();
			GenericProperty property =
				new GenericProperty("property with 4", configuration, shrinkablesGenerator, forAllFunction, tryLifecycleContextSupplier);

			PropertyCheckResult result = property.check(TestHelper.reporter(), new Reporting[0]);

			assertThat(result.propertyName()).isEqualTo("property with 4");
			assertThat(result.checkStatus()).isEqualTo(PropertyCheckResult.CheckStatus.FAILED);
			assertThat(result.countTries()).isEqualTo(failingTry);
			assertThat(result.countChecks()).isEqualTo(failingTry);
			assertThat(result.throwable()).isPresent();

			assertThat(result.falsifiedParameters()).isPresent();
			assertThat(result.falsifiedParameters().get()).containsExactly(failingTry, 1, 1, 1);
			assertThat(result.originalSample()).isPresent();
			assertThat(result.originalSample().get().shrinkables()).hasSize(4);
		}

	}

	private ParametersGenerator randomizedShrinkablesGenerator(Arbitrary<Object>... arbitraries) {
		JqwikRandom random = SourceOfRandomness.current();
		List<Arbitrary<Object>> arbitraryList = Arrays.stream(arbitraries).collect(Collectors.toList());
		List<RandomGenerator<Object>> generators = arbitraryList
			.stream()
			.map(arbitrary -> arbitrary.generator(9999, true))
			.collect(Collectors.toList());

		return new ParametersGenerator() {

			private int index = 0;

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public List<Shrinkable<Object>> next(TryLifecycleContext context) {
				return generators
					.stream()
					.map(generator -> generator.next(random))
					.peek(ignore -> index++)
					.collect(Collectors.toList());
			}

			@Override
			public int edgeCasesTotal() {
				return 0;
			}

			@Override
			public int edgeCasesTried() {
				return 0;
			}

			@Override
			public GenerationInfo generationInfo(String randomSeed) {
				return new GenerationInfo(randomSeed, index);
			}

			@Override
			public void reset() {
				index = 0;
			}
		};
	}

	private ParametersGenerator emptyShrinkablesGenerator() {
		return new ParametersGenerator() {
			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public List<Shrinkable<Object>> next(TryLifecycleContext context) {
				return new ArrayList<>();
			}

			@Override
			public int edgeCasesTotal() {
				return 0;
			}

			@Override
			public int edgeCasesTried() {
				return 0;
			}

			@Override
			public GenerationInfo generationInfo(String randomSeed) {
				return new GenerationInfo(randomSeed, 0);
			}

			@Override
			public void reset() {
			}
		};
	}

	private ParametersGenerator finiteShrinkablesGenerator(int... values) {
		Iterator<Integer> valuesIterator = Arrays.stream(values).iterator();

		return new ParametersGenerator() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return valuesIterator.hasNext();
			}

			@Override
			public List<Shrinkable<Object>> next(TryLifecycleContext context) {
				Shrinkable<Object> shrinkable = Shrinkable.unshrinkable(valuesIterator.next());
				index++;
				return Collections.singletonList(shrinkable);
			}

			@Override
			public int edgeCasesTotal() {
				return 0;
			}

			@Override
			public int edgeCasesTried() {
				return 0;
			}

			@Override
			public GenerationInfo generationInfo(String randomSeed) {
				return new GenerationInfo(randomSeed, index);
			}

			@Override
			public void reset() {
				index = 0;
			}
		};
	}

}
