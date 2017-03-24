package net.jqwik.execution.properties;

import static org.junit.platform.engine.TestExecutionResult.*;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.*;
import org.opentest4j.*;

import javaslang.*;
import javaslang.control.*;
import javaslang.test.*;

/**
 * Wraps javaslang's property checking
 */
public class CheckedProperty {

	private final String propertyName;
	private final CheckedFunction forAllFunction;
	private final List<Parameter> forAllParameters;
	private final ArbitraryProvider arbitraryProvider;
	private final int tries;

	public CheckedProperty(String propertyName, CheckedFunction forAllFunction, List<Parameter> forAllParameters,
			ArbitraryProvider arbitraryProvider, int tries) {
		this.propertyName = propertyName;
		this.forAllFunction = forAllFunction;
		this.forAllParameters = forAllParameters;
		this.arbitraryProvider = arbitraryProvider;
		this.tries = tries;
	}

	public TestExecutionResult check() {
		try {
			CheckResult result = createJavaSlangProperty().check(Checkable.DEFAULT_SIZE, tries);
			if (result.isSatisfied())
				return successful();
			else {
				String propertyFailedMessage = String.format("Property [%s] failed: %s", propertyName, result.toString());
				return failed(new AssertionFailedError(propertyFailedMessage));
			}
		} catch (CannotFindArbitraryException cannotFindArbitraryException) {
			return aborted(cannotFindArbitraryException);
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
		CheckedFunction1<Object, Boolean> function = createCheckedFunction1();
		return Property.def(propertyName).forAll(a1).suchThat(function);
	}

	private CheckedFunction1<Object, Boolean> createCheckedFunction1() {
		return p1 -> forAllFunction.apply(new Object[] { p1 });
	}

	private Checkable createProperty2() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> a2 = findArbitrary(forAllParameters.get(1));
		CheckedFunction2<Object, Object, Boolean> function = createCheckedFunction2();
		return Property.def(propertyName).forAll(a1, a2).suchThat(function);
	}

	private CheckedFunction2<Object, Object, Boolean> createCheckedFunction2() {
		return (p1, p2) -> forAllFunction.apply(new Object[] { p1, p2 });
	}

	private Checkable createProperty3() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> a2 = findArbitrary(forAllParameters.get(1));
		Arbitrary<Object> a3 = findArbitrary(forAllParameters.get(2));
		CheckedFunction3<Object, Object, Object, Boolean> function = createCheckedFunction3();
		return Property.def(propertyName).forAll(a1, a2, a3).suchThat(function);
	}

	private CheckedFunction3<Object, Object, Object, Boolean> createCheckedFunction3() {
		return (p1, p2, p3) -> forAllFunction.apply(new Object[] { p1, p2, p3 });
	}

	private Checkable createProperty4() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> a2 = findArbitrary(forAllParameters.get(1));
		Arbitrary<Object> a3 = findArbitrary(forAllParameters.get(2));
		Arbitrary<Object> a4 = findArbitrary(forAllParameters.get(3));
		CheckedFunction4<Object, Object, Object, Object, Boolean> function = createCheckedFunction4();
		return Property.def(propertyName).forAll(a1, a2, a3, a4).suchThat(function);
	}

	private CheckedFunction4<Object, Object, Object, Object, Boolean> createCheckedFunction4() {
		return (p1, p2, p3, p4) -> forAllFunction.apply(new Object[] { p1, p2, p3, p4 });
	}

	private Checkable createProperty5() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> a2 = findArbitrary(forAllParameters.get(1));
		Arbitrary<Object> a3 = findArbitrary(forAllParameters.get(2));
		Arbitrary<Object> a4 = findArbitrary(forAllParameters.get(3));
		Arbitrary<Object> a5 = findArbitrary(forAllParameters.get(4));
		CheckedFunction5<Object, Object, Object, Object, Object, Boolean> function = createCheckedFunction5();
		return Property.def(propertyName).forAll(a1, a2, a3, a4, a5).suchThat(function);
	}

	private CheckedFunction5<Object, Object, Object, Object, Object, Boolean> createCheckedFunction5() {
		return (p1, p2, p3, p4, p5) -> forAllFunction.apply(new Object[] { p1, p2, p3, p4, p5 });
	}

	private Checkable createProperty6() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> a2 = findArbitrary(forAllParameters.get(1));
		Arbitrary<Object> a3 = findArbitrary(forAllParameters.get(2));
		Arbitrary<Object> a4 = findArbitrary(forAllParameters.get(3));
		Arbitrary<Object> a5 = findArbitrary(forAllParameters.get(4));
		Arbitrary<Object> a6 = findArbitrary(forAllParameters.get(5));
		CheckedFunction6<Object, Object, Object, Object, Object, Object, Boolean> function = createCheckedFunction6();
		return Property.def(propertyName).forAll(a1, a2, a3, a4, a5, a6).suchThat(function);
	}

	private CheckedFunction6<Object, Object, Object, Object, Object, Object, Boolean> createCheckedFunction6() {
		return (p1, p2, p3, p4, p5, p6) -> forAllFunction.apply(new Object[] { p1, p2, p3, p4, p5, p6 });
	}

	private Checkable createProperty7() {
		Arbitrary<Object> a1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> a2 = findArbitrary(forAllParameters.get(1));
		Arbitrary<Object> a3 = findArbitrary(forAllParameters.get(2));
		Arbitrary<Object> a4 = findArbitrary(forAllParameters.get(3));
		Arbitrary<Object> a5 = findArbitrary(forAllParameters.get(4));
		Arbitrary<Object> a6 = findArbitrary(forAllParameters.get(5));
		Arbitrary<Object> a7 = findArbitrary(forAllParameters.get(6));
		CheckedFunction7<Object, Object, Object, Object, Object, Object, Object, Boolean> function = createCheckedFunction7();
		return Property.def(propertyName).forAll(a1, a2, a3, a4, a5, a6, a7).suchThat(function);
	}

	private CheckedFunction7<Object, Object, Object, Object, Object, Object, Object, Boolean> createCheckedFunction7() {
		return (p1, p2, p3, p4, p5, p6, p7) -> forAllFunction.apply(new Object[] { p1, p2, p3, p4, p5, p6, p7 });
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
		CheckedFunction8<Object, Object, Object, Object, Object, Object, Object, Object, Boolean> function = createCheckedFunction8();
		return Property.def(propertyName).forAll(a1, a2, a3, a4, a5, a6, a7, a8).suchThat(function);
	}

	private CheckedFunction8<Object, Object, Object, Object, Object, Object, Object, Object, Boolean> createCheckedFunction8() {
		return (p1, p2, p3, p4, p5, p6, p7, p8) -> forAllFunction.apply(new Object[] { p1, p2, p3, p4, p5, p6, p7, p8 });
	}

	private Checkable erroneousCheckable(int paramsCount) {
		return (randomNumberGenerator, size, tries) -> {
			String errorMessage = String.format("%s @ForAll parameters is too many. Max is 8.", paramsCount);
			return new ErroneousCheckResult(errorMessage);
		};
	}

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
