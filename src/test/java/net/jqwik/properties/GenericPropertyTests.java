package net.jqwik.properties;

import static org.assertj.core.api.Assertions.*;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

class GenericPropertyTests {

	@Group
	class OneParameter {

		private final Function<List<Object>, Boolean> exactlyOneInteger = args -> args.size() == 1 && args.get(0) instanceof Integer;

		@Example
		void satisfied() {
			ForAllSpy forAllFunction = new ForAllSpy(trie -> true, exactlyOneInteger);

			Arbitrary<Integer> arbitrary = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			List<Arbitrary> arbitraries = arbitraries(arbitrary);

			GenericProperty property = new GenericProperty("satisfied property", arbitraries, forAllFunction);
			PropertyCheckResult result = property.check(2, 42L);

			assertThat(forAllFunction.countCalls()).isEqualTo(2);

			assertThat(result.propertyName()).isEqualTo("satisfied property");
			assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.SATISFIED);
			assertThat(result.countTries()).isEqualTo(2);
			assertThat(result.countChecks()).isEqualTo(2);
			assertThat(result.randomSeed()).isEqualTo(42L);
			assertThat(result.throwable()).isNotPresent();
			assertThat(result.sample()).isNotPresent();
		}

		@Example
		void falsified() {
			int failingTry = 5;

			ForAllSpy forAllFunction = new ForAllSpy(trie -> trie < failingTry, exactlyOneInteger);

			Arbitrary<Integer> arbitrary = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			List<Arbitrary> arbitraries = arbitraries(arbitrary);

			GenericProperty property = new GenericProperty("falsified property", arbitraries, forAllFunction);
			PropertyCheckResult result = property.check(10, 41L);

			assertThat(forAllFunction.countCalls()).isEqualTo(failingTry + 1); // Shrinking adds one call

			assertThat(result.propertyName()).isEqualTo("falsified property");
			assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.FALSIFIED);
			assertThat(result.countTries()).isEqualTo(failingTry);
			assertThat(result.countChecks()).isEqualTo(failingTry);
			assertThat(result.randomSeed()).isEqualTo(41L);
			assertThat(result.throwable()).isNotPresent();

			assertThat(result.sample()).isPresent();
			assertThat(result.sample().get()).containsExactly(failingTry);
		}

		@Example
		void falsifiedThroughAssertionError() {
			AssertionError assertionError = new AssertionError("test");
			ForAllSpy forAllFunction = new ForAllSpy(trie -> {
				throw assertionError;
			}, exactlyOneInteger);

			Arbitrary<Integer> arbitrary = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			List<Arbitrary> arbitraries = arbitraries(arbitrary);

			GenericProperty property = new GenericProperty("falsified property", arbitraries, forAllFunction);
			PropertyCheckResult result = property.check(10, 41L);

			assertThat(forAllFunction.countCalls()).isEqualTo(2); // Shrinking adds one call

			assertThat(result.propertyName()).isEqualTo("falsified property");
			assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.FALSIFIED);
			assertThat(result.countTries()).isEqualTo(1);
			assertThat(result.countChecks()).isEqualTo(1);
			assertThat(result.randomSeed()).isEqualTo(41L);

			assertThat(result.throwable()).isPresent();
			assertThat(result.throwable().get()).isSameAs(assertionError);

			assertThat(result.sample()).isPresent();
			assertThat(result.sample().get()).containsExactly(1);
		}

		@Example
		void satisfiedWithRejectedAssumptions() {
			IntPredicate isEven = aNumber -> aNumber % 2 == 0;

			ForAllSpy forAllFunction = new ForAllSpy(aTry -> {
				Assumptions.assumeThat(isEven.test(aTry));
				assertThat(isEven.test(aTry));
				return true;
			}, exactlyOneInteger);

			Arbitrary<Integer> arbitrary = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			List<Arbitrary> arbitraries = arbitraries(arbitrary);

			GenericProperty property = new GenericProperty("satisfied property", arbitraries, forAllFunction);
			PropertyCheckResult result = property.check(10, 42L);

			assertThat(forAllFunction.countCalls()).isEqualTo(10);

			assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.SATISFIED);
			assertThat(result.countTries()).isEqualTo(10);
			assertThat(result.countChecks()).isEqualTo(5);
			assertThat(result.randomSeed()).isEqualTo(42L);
			assertThat(result.throwable()).isNotPresent();
			assertThat(result.sample()).isNotPresent();
		}

		@Example
		void exhausted() {
			ForAllSpy forAllFunction = new ForAllSpy(aTry -> {
				Assumptions.assumeThat(false);
				return true;
			}, exactlyOneInteger);

			Arbitrary<Integer> arbitrary = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			List<Arbitrary> arbitraries = arbitraries(arbitrary);

			GenericProperty property = new GenericProperty("exhausted property", arbitraries, forAllFunction);
			PropertyCheckResult result = property.check(10, 42L);

			assertThat(forAllFunction.countCalls()).isEqualTo(10);

			assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.EXHAUSTED);
			assertThat(result.countTries()).isEqualTo(10);
			assertThat(result.countChecks()).isEqualTo(0);
			assertThat(result.randomSeed()).isEqualTo(42L);
			assertThat(result.throwable()).isNotPresent();
			assertThat(result.sample()).isNotPresent();
		}

		@Example
		void exceptionInForAllFunctionMakesPropertyErroneous() {
			int erroneousTry = 5;
			RuntimeException thrownException = new RuntimeException("thrown in test");

			ForAllSpy forAllFunction = new ForAllSpy(aTry -> {
				if (aTry == erroneousTry)
					throw thrownException;
				return true;
			}, exactlyOneInteger);

			Arbitrary<Integer> arbitrary = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			List<Arbitrary> arbitraries = arbitraries(arbitrary);

			GenericProperty property = new GenericProperty("erroneous property", arbitraries, forAllFunction);
			PropertyCheckResult result = property.check(10, 42L);

			assertThat(forAllFunction.countCalls()).isEqualTo(erroneousTry);

			assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.ERRONEOUS);
			assertThat(result.countTries()).isEqualTo(erroneousTry);
			assertThat(result.countChecks()).isEqualTo(erroneousTry);
			assertThat(result.randomSeed()).isEqualTo(42L);

			assertThat(result.throwable()).isPresent();
			assertThat(result.throwable().get()).isSameAs(thrownException);

			assertThat(result.sample()).isPresent();
			assertThat(result.sample().get()).containsExactly(erroneousTry);
		}
	}

	@Group
	class NoParameter {
		@Example
		void checkPropertyOnlyOnce() {
			CheckedFunction forAllFunction = args -> {
				assertThat(args).isEmpty();
				return true;
			};

			GenericProperty property = new GenericProperty("satisfied property", arbitraries(), forAllFunction);
			PropertyCheckResult result = property.check(2, 42L);

			assertThat(result.propertyName()).isEqualTo("satisfied property");
			assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.SATISFIED);
			assertThat(result.countTries()).isEqualTo(1);
			assertThat(result.countChecks()).isEqualTo(1);
			assertThat(result.randomSeed()).isEqualTo(42L);
			assertThat(result.throwable()).isNotPresent();
			assertThat(result.sample()).isNotPresent();
		}

		@Example
		void evenIfItFails() {
			CheckedFunction forAllFunction = args -> {
				assertThat(args).isEmpty();
				return false;
			};

			GenericProperty property = new GenericProperty("failing property", arbitraries(), forAllFunction);
			PropertyCheckResult result = property.check(2, 42L);

			assertThat(result.propertyName()).isEqualTo("failing property");
			assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.FALSIFIED);
			assertThat(result.countTries()).isEqualTo(1);
			assertThat(result.countChecks()).isEqualTo(1);
			assertThat(result.randomSeed()).isEqualTo(42L);
			assertThat(result.throwable()).isNotPresent();

			assertThat(result.sample()).isPresent();
			assertThat(result.sample().get()).isEmpty();
		}

		@Example
		void evenIfItThrowsException() {
			CheckedFunction forAllFunction = args -> {
				assertThat(args).isEmpty();
				throw new RuntimeException();
			};

			GenericProperty property = new GenericProperty("failing property", arbitraries(), forAllFunction);
			PropertyCheckResult result = property.check(2, 42L);

			assertThat(result.propertyName()).isEqualTo("failing property");
			assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.ERRONEOUS);
			assertThat(result.countTries()).isEqualTo(1);
			assertThat(result.countChecks()).isEqualTo(1);
			assertThat(result.randomSeed()).isEqualTo(42L);

			assertThat(result.throwable()).isPresent();
			assertThat(result.throwable().get()).isInstanceOf(RuntimeException.class);

			assertThat(result.sample()).isPresent();
			assertThat(result.sample().get()).isEmpty();
		}

	}

	@Group
	class ManyParameters {
		private final Function<List<Object>, Boolean> exactlyOneInteger = args -> args.size() == 1 && args.get(0) instanceof Integer;

		@Example
		void twoParametersSatisfied() {
			CheckedFunction forAllFunction = args -> {
				assertThat(args).size().isEqualTo(2);
				assertThat(args.get(0)).isInstanceOf(Integer.class);
				assertThat(args.get(1)).isInstanceOf(Integer.class);
				return true;
			};

			Arbitrary<Integer> arbitrary1 = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			Arbitrary<Integer> arbitrary2 = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			List<Arbitrary> arbitraries = arbitraries(arbitrary1, arbitrary2);

			GenericProperty property = new GenericProperty("property with 2", arbitraries, forAllFunction);
			PropertyCheckResult result = property.check(5, 4242L);

			assertThat(result.propertyName()).isEqualTo("property with 2");
			assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.SATISFIED);
			assertThat(result.countTries()).isEqualTo(5);
			assertThat(result.countChecks()).isEqualTo(5);
			assertThat(result.randomSeed()).isEqualTo(4242L);
			assertThat(result.throwable()).isNotPresent();
			assertThat(result.sample()).isNotPresent();
		}

		@Example
		void fourParametersFalsified() {
			int failingTry = 5;

			CheckedFunction forAllFunction = args -> {
				assertThat(args).size().isEqualTo(4);
				return ((int) args.get(0)) < failingTry;
			};

			Arbitrary<Integer> arbitrary1 = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			Arbitrary<Integer> arbitrary2 = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			Arbitrary<Integer> arbitrary3 = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			Arbitrary<Integer> arbitrary4 = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			List<Arbitrary> arbitraries = arbitraries(arbitrary1, arbitrary2, arbitrary3, arbitrary4);

			GenericProperty property = new GenericProperty( "property with 4", arbitraries, forAllFunction);
			PropertyCheckResult result = property.check(10, 4141L);

			assertThat(result.propertyName()).isEqualTo("property with 4");
			assertThat(result.status()).isEqualTo(PropertyCheckResult.Status.FALSIFIED);
			assertThat(result.countTries()).isEqualTo(failingTry);
			assertThat(result.countChecks()).isEqualTo(failingTry);
			assertThat(result.randomSeed()).isEqualTo(4141L);
			assertThat(result.throwable()).isNotPresent();

			assertThat(result.sample()).isPresent();
			assertThat(result.sample().get()).containsExactly(failingTry, failingTry, failingTry, failingTry);
		}


	}

	private List<Arbitrary> arbitraries(Arbitrary... arbitraries) {
		return Arrays.asList(arbitraries);
	}

}
