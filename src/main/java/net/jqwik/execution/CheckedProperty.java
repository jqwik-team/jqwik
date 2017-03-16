package net.jqwik.execution;

import static org.junit.platform.engine.TestExecutionResult.*;

import java.lang.reflect.Parameter;
import java.util.List;

import org.junit.platform.engine.TestExecutionResult;
import org.opentest4j.AssertionFailedError;

import javaslang.CheckedFunction1;
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
		Class<?> paramType1 = int.class;

		Arbitrary<Object> arbitrary = new GenericWrapper(Arbitrary.integer());
		CheckedFunction1<Object, Boolean> function1 = createCheckedFunction();
		return Property.def(propertyName).forAll(arbitrary).suchThat(function1);
	}

	private CheckedFunction1<Object, Boolean> createCheckedFunction() {
		return p1 -> forAllFunction.apply(new Object[] {p1});
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
