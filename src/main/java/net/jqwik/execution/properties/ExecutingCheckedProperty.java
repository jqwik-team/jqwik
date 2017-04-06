package net.jqwik.execution.properties;

import java.lang.reflect.*;
import java.util.*;

import javaslang.*;
import javaslang.control.*;
import javaslang.test.*;

/**
 * Wraps javaslang's property checking
 */
public class ExecutingCheckedProperty implements CheckedProperty {

	public final String propertyName;
	public final CheckedFunction assumeFunction;
	public final CheckedFunction forAllFunction;
	public final List<Parameter> forAllParameters;
	public final ArbitraryProvider arbitraryProvider;
	public final int tries;
	public final long randomSeed;

	public ExecutingCheckedProperty(String propertyName, CheckedFunction assumeFunction, CheckedFunction forAllFunction,
			List<Parameter> forAllParameters, ArbitraryProvider arbitraryProvider, int tries, long randomSeed) {
		this.propertyName = propertyName;
		this.assumeFunction = assumeFunction;
		this.forAllFunction = forAllFunction;
		this.forAllParameters = forAllParameters;
		this.arbitraryProvider = arbitraryProvider;
		this.tries = tries;
		this.randomSeed = randomSeed;
	}

	@Override
	public PropertyExecutionResult check() {
		// Long.MIN_VALUE is the default for Property.seed() annotation property
		long effectiveSeed = randomSeed == Long.MIN_VALUE ? Checkable.RNG.get().nextLong() : randomSeed;
		try {
			Random random = new Random(effectiveSeed);
			CheckResult result = createJavaSlangProperty().check(random, Checkable.DEFAULT_SIZE, tries);
			if (result.isSatisfied())
				return PropertyExecutionResult.successful(effectiveSeed);
			else {
				String propertyFailedMessage = String.format("Property [%s] failed: %s", propertyName, result.toString());
				return PropertyExecutionResult.failed(propertyFailedMessage, effectiveSeed);
			}
		} catch (CannotFindArbitraryException cannotFindArbitraryException) {
			return PropertyExecutionResult.aborted(cannotFindArbitraryException, effectiveSeed);
		}
	}

	private Arbitrary<Object> findArbitrary(Parameter parameter) {
		Optional<Arbitrary<Object>> arbitraryOptional = arbitraryProvider.forParameter(parameter);
		if (!arbitraryOptional.isPresent())
			throw new CannotFindArbitraryException(parameter);
		return arbitraryOptional.get();
	}

	private Checkable createJavaSlangProperty() {
		if (forAllParameters.size() == 1) {
			return createProperty1();
		}
		if (forAllParameters.size() == 2) {
			return createProperty2();
		}
		if (forAllParameters.size() == 3) {
			return createProperty3();
		}
		if (forAllParameters.size() == 4) {
			return createProperty4();
		}
		if (forAllParameters.size() == 5) {
			return createProperty5();
		}
		if (forAllParameters.size() == 6) {
			return createProperty6();
		}
		if (forAllParameters.size() == 7) {
			return createProperty7();
		}
		if (forAllParameters.size() == 8) {
			return createProperty8();
		}
		return erroneousCheckable(forAllParameters.size());
	}

	private Checkable createProperty1() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		CheckedFunction1<Object, Boolean> assume = createCheckedFunction1(assumeFunction);
		CheckedFunction1<Object, Boolean> implies = createCheckedFunction1(forAllFunction);
		return Property.def(propertyName).forAll(a1).suchThat(assume).implies(implies);
	}

	private CheckedFunction1<Object, Boolean> createCheckedFunction1(CheckedFunction function) {
		return p1 -> function.apply(new Object[] { p1 });
	}

	private Checkable createProperty2() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> a2 = findArbitrary(forAllParameters.get(1));
		CheckedFunction2<Object, Object, Boolean> assume = createCheckedFunction2(assumeFunction);
		CheckedFunction2<Object, Object, Boolean> implies = createCheckedFunction2(forAllFunction);
		return Property.def(propertyName).forAll(a1, a2).suchThat(assume).implies(implies);
	}

	private CheckedFunction2<Object, Object, Boolean> createCheckedFunction2(CheckedFunction function) {
		return (p1, p2) -> function.apply(new Object[] { p1, p2 });
	}

	private Checkable createProperty3() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> a2 = findArbitrary(forAllParameters.get(1));
		Arbitrary<Object> a3 = findArbitrary(forAllParameters.get(2));
		CheckedFunction3<Object, Object, Object, Boolean> assume = createCheckedFunction3(assumeFunction);
		CheckedFunction3<Object, Object, Object, Boolean> implies = createCheckedFunction3(forAllFunction);
		return Property.def(propertyName).forAll(a1, a2, a3).suchThat(assume).implies(implies);
	}

	private CheckedFunction3<Object, Object, Object, Boolean> createCheckedFunction3(CheckedFunction function) {
		return (p1, p2, p3) -> function.apply(new Object[] { p1, p2, p3 });
	}

	private Checkable createProperty4() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> a2 = findArbitrary(forAllParameters.get(1));
		Arbitrary<Object> a3 = findArbitrary(forAllParameters.get(2));
		Arbitrary<Object> a4 = findArbitrary(forAllParameters.get(3));
		CheckedFunction4<Object, Object, Object, Object, Boolean> assume = createCheckedFunction4(assumeFunction);
		CheckedFunction4<Object, Object, Object, Object, Boolean> implies = createCheckedFunction4(forAllFunction);
		return Property.def(propertyName).forAll(a1, a2, a3, a4).suchThat(assume).implies(implies);
	}

	private CheckedFunction4<Object, Object, Object, Object, Boolean> createCheckedFunction4(CheckedFunction function) {
		return (p1, p2, p3, p4) -> function.apply(new Object[] { p1, p2, p3, p4 });
	}

	private Checkable createProperty5() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> a2 = findArbitrary(forAllParameters.get(1));
		Arbitrary<Object> a3 = findArbitrary(forAllParameters.get(2));
		Arbitrary<Object> a4 = findArbitrary(forAllParameters.get(3));
		Arbitrary<Object> a5 = findArbitrary(forAllParameters.get(4));
		CheckedFunction5<Object, Object, Object, Object, Object, Boolean> assume = createCheckedFunction5(assumeFunction);
		CheckedFunction5<Object, Object, Object, Object, Object, Boolean> implies = createCheckedFunction5(forAllFunction);
		return Property.def(propertyName).forAll(a1, a2, a3, a4, a5).suchThat(assume).implies(implies);
	}

	private CheckedFunction5<Object, Object, Object, Object, Object, Boolean> createCheckedFunction5(CheckedFunction function) {
		return (p1, p2, p3, p4, p5) -> function.apply(new Object[] { p1, p2, p3, p4, p5 });
	}

	private Checkable createProperty6() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> a2 = findArbitrary(forAllParameters.get(1));
		Arbitrary<Object> a3 = findArbitrary(forAllParameters.get(2));
		Arbitrary<Object> a4 = findArbitrary(forAllParameters.get(3));
		Arbitrary<Object> a5 = findArbitrary(forAllParameters.get(4));
		Arbitrary<Object> a6 = findArbitrary(forAllParameters.get(5));
		CheckedFunction6<Object, Object, Object, Object, Object, Object, Boolean> assume = createCheckedFunction6(assumeFunction);
		CheckedFunction6<Object, Object, Object, Object, Object, Object, Boolean> implies = createCheckedFunction6(forAllFunction);
		return Property.def(propertyName).forAll(a1, a2, a3, a4, a5, a6).suchThat(assume).implies(implies);
	}

	private CheckedFunction6<Object, Object, Object, Object, Object, Object, Boolean> createCheckedFunction6(CheckedFunction function) {
		return (p1, p2, p3, p4, p5, p6) -> function.apply(new Object[] { p1, p2, p3, p4, p5, p6 });
	}

	private Checkable createProperty7() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> a2 = findArbitrary(forAllParameters.get(1));
		Arbitrary<Object> a3 = findArbitrary(forAllParameters.get(2));
		Arbitrary<Object> a4 = findArbitrary(forAllParameters.get(3));
		Arbitrary<Object> a5 = findArbitrary(forAllParameters.get(4));
		Arbitrary<Object> a6 = findArbitrary(forAllParameters.get(5));
		Arbitrary<Object> a7 = findArbitrary(forAllParameters.get(6));
		CheckedFunction7<Object, Object, Object, Object, Object, Object, Object, Boolean> assume = createCheckedFunction7(assumeFunction);
		CheckedFunction7<Object, Object, Object, Object, Object, Object, Object, Boolean> implies = createCheckedFunction7(forAllFunction);
		return Property.def(propertyName).forAll(a1, a2, a3, a4, a5, a6, a7).suchThat(assume).implies(implies);
	}

	private CheckedFunction7<Object, Object, Object, Object, Object, Object, Object, Boolean> createCheckedFunction7(
			CheckedFunction function) {
		return (p1, p2, p3, p4, p5, p6, p7) -> function.apply(new Object[] { p1, p2, p3, p4, p5, p6, p7 });
	}

	private Checkable createProperty8() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> a2 = findArbitrary(forAllParameters.get(1));
		Arbitrary<Object> a3 = findArbitrary(forAllParameters.get(2));
		Arbitrary<Object> a4 = findArbitrary(forAllParameters.get(3));
		Arbitrary<Object> a5 = findArbitrary(forAllParameters.get(4));
		Arbitrary<Object> a6 = findArbitrary(forAllParameters.get(5));
		Arbitrary<Object> a7 = findArbitrary(forAllParameters.get(6));
		Arbitrary<Object> a8 = findArbitrary(forAllParameters.get(7));
		CheckedFunction8<Object, Object, Object, Object, Object, Object, Object, Object, Boolean> assume = createCheckedFunction8(
				assumeFunction);
		CheckedFunction8<Object, Object, Object, Object, Object, Object, Object, Object, Boolean> implies = createCheckedFunction8(
				forAllFunction);
		return Property.def(propertyName).forAll(a1, a2, a3, a4, a5, a6, a7, a8).suchThat(assume).implies(implies);
	}

	private CheckedFunction8<Object, Object, Object, Object, Object, Object, Object, Object, Boolean> createCheckedFunction8(
			CheckedFunction function) {
		return (p1, p2, p3, p4, p5, p6, p7, p8) -> function.apply(new Object[] { p1, p2, p3, p4, p5, p6, p7, p8 });
	}

	private Checkable erroneousCheckable(int paramsCount) {
		return (randomNumberGenerator, size, tries) -> {
			String errorMessage = String.format("%s @ForAll parameters is too many. Max is 8.", paramsCount);
			return new ErroneousCheckResult(errorMessage);
		};
	}

	@Override
	public int getTries() {
		return tries;
	}

	private class ErroneousCheckResult implements CheckResult {
		private final String errorMessage;

		ErroneousCheckResult(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		@Override
		public boolean isSatisfied() {
			return false;
		}

		@Override
		public boolean isFalsified() {
			return false;
		}

		@Override
		public boolean isErroneous() {
			return true;
		}

		@Override
		public boolean isExhausted() {
			return false;
		}

		@Override
		public String propertyName() {
			return propertyName;
		}

		@Override
		public int count() {
			return 0;
		}

		@Override
		public Option<Tuple> sample() {
			return null;
		}

		@Override
		public Option<Error> error() {
			return null;
		}

		@Override
		public String toString() {
			return errorMessage;
		}

	}
}
