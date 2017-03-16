package net.jqwik.execution;

import javaslang.*;
import javaslang.control.Option;
import javaslang.test.*;
import org.junit.platform.engine.TestExecutionResult;
import org.opentest4j.AssertionFailedError;

import java.lang.reflect.Parameter;
import java.util.List;

import static org.junit.platform.engine.TestExecutionResult.failed;
import static org.junit.platform.engine.TestExecutionResult.successful;

/**
 * Wraps javaslang's property checking
 */
public class CheckedProperty {

	private final String propertyName;
	private final CheckedFunction forAllFunction;
	private final List<Parameter> forAllParameters;

	public CheckedProperty(String propertyName, CheckedFunction forAllFunction, List<Parameter> forAllParameters) {
		this.propertyName = propertyName;
		this.forAllFunction = forAllFunction;
		this.forAllParameters = forAllParameters;
	}

	public TestExecutionResult check() {
		CheckResult result = createJavaSlangProperty().check();
		if (result.isSatisfied())
			return successful();
		else {
			String propertyFailedMessage = String.format("Property [%s] failed: %s", propertyName, result.toString());
			return failed(new AssertionFailedError(propertyFailedMessage));
		}
	}

	private GenericWrapper findArbitrary(Parameter parameter) {
		// Todo: Find correct arbitrary for type
		return new GenericWrapper(Arbitrary.integer());
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
		return erroneousCheckable();
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
		Arbitrary<Object> a5 = findArbitrary(forAllParameters.get(3));
		CheckedFunction5<Object, Object, Object, Object, Object, Boolean> function = createCheckedFunction5();
		return Property.def(propertyName).forAll(a1, a2, a3, a4, a5).suchThat(function);
	}

	private CheckedFunction5<Object, Object, Object, Object, Object, Boolean> createCheckedFunction5() {
		return (p1, p2, p3, p4, p5) -> forAllFunction.apply(new Object[] { p1, p2, p3, p4, p5 });
	}

	private Checkable erroneousCheckable() {
		return (randomNumberGenerator, size, tries) -> new ErroneousCheckResult("Too many @ForAll parameters. Max is 8.");
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

class GenericWrapper implements Arbitrary<Object> {

	private final Arbitrary<?> wrapped;

	GenericWrapper(Arbitrary<?> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public Gen<Object> apply(int size) {
		return (Gen<Object>) wrapped.apply(size);
	}
}


