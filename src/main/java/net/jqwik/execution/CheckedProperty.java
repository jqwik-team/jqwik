package net.jqwik.execution;

import static org.junit.platform.engine.TestExecutionResult.*;

import java.lang.reflect.Parameter;
import java.util.List;

import javaslang.CheckedFunction2;
import javaslang.CheckedFunction3;
import org.junit.platform.engine.TestExecutionResult;
import org.opentest4j.AssertionFailedError;

import javaslang.CheckedFunction1;
import javaslang.Tuple;
import javaslang.control.Option;
import javaslang.test.*;

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
		return erroneousCheckable();
	}

	private Checkable createProperty1() {
		Arbitrary<Object> arbitrary1 = findArbitrary(forAllParameters.get(0));
		CheckedFunction1<Object, Boolean> function = createCheckedFunction1();
		return Property.def(propertyName).forAll(arbitrary1).suchThat(function);
	}

	private CheckedFunction1<Object, Boolean> createCheckedFunction1() {
		return p1 -> forAllFunction.apply(new Object[] {p1});
	}

	private Checkable createProperty2() {
		Arbitrary<Object> arbitrary1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> arbitrary2 = findArbitrary(forAllParameters.get(1));
		CheckedFunction2<Object, Object, Boolean> function = createCheckedFunction2();
		return Property.def(propertyName).forAll(arbitrary1, arbitrary2).suchThat(function);
	}

	private CheckedFunction2<Object, Object, Boolean> createCheckedFunction2() {
		return (p1, p2) -> forAllFunction.apply(new Object[] {p1, p2});
	}

	private Checkable createProperty3() {
		Arbitrary<Object> arbitrary1 = findArbitrary(forAllParameters.get(0));
		Arbitrary<Object> arbitrary2 = findArbitrary(forAllParameters.get(1));
		Arbitrary<Object> arbitrary3 = findArbitrary(forAllParameters.get(2));
		CheckedFunction3<Object, Object, Object, Boolean> function = createCheckedFunction3();
		return Property.def(propertyName).forAll(arbitrary1, arbitrary2, arbitrary3).suchThat(function);
	}

	private CheckedFunction3<Object, Object, Object, Boolean> createCheckedFunction3() {
		return (p1, p2, p3) -> forAllFunction.apply(new Object[] {p1, p2, p3});
	}


	private GenericWrapper findArbitrary(Parameter parameter) {
		//Todo: Find correct arbitrary for type
		return new GenericWrapper(Arbitrary.integer());
	}

	private Checkable erroneousCheckable() {
		return (randomNumberGenerator, size, tries) -> new CheckResult() {
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
				return "Too many @ForAll parameters. Max is 8.";
			}
		};
	}

	private static class GenericWrapper implements Arbitrary<Object> {

		private final Arbitrary<?> wrapped;

		GenericWrapper(Arbitrary<?> wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public Gen<Object> apply(int size) {
			return (Gen<Object>) wrapped.apply(size);
		}
	}
}
