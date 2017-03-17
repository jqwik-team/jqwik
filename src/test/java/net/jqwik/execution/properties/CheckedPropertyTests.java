package net.jqwik.execution.properties;

import static net.jqwik.execution.properties.ParameterHelper.getParametersFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.TestExecutionResult.Status.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.platform.engine.TestExecutionResult;

import javaslang.test.Arbitrary;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;

class CheckedPropertyTests {

	@Example
	void intParametersSuccess() {
		intOnlyExample("prop1", params -> params.length == 1, SUCCESSFUL);
		intOnlyExample("prop2", params -> params.length == 2, SUCCESSFUL);
		intOnlyExample("prop3", params -> params.length == 3, SUCCESSFUL);
		intOnlyExample("prop4", params -> params.length == 4, SUCCESSFUL);
		intOnlyExample("prop5", params -> params.length == 5, SUCCESSFUL);
		intOnlyExample("prop6", params -> params.length == 6, SUCCESSFUL);
		intOnlyExample("prop7", params -> params.length == 7, SUCCESSFUL);
		intOnlyExample("prop8", params -> params.length == 8, SUCCESSFUL);
	}

	@Example
	void intParametersFailure() {
		intOnlyExample("prop1", params -> false, FAILED);
		intOnlyExample("prop2", params -> false, FAILED);
		intOnlyExample("prop3", params -> false, FAILED);
		intOnlyExample("prop4", params -> false, FAILED);
		intOnlyExample("prop5", params -> false, FAILED);
		intOnlyExample("prop6", params -> false, FAILED);
		intOnlyExample("prop7", params -> false, FAILED);
		intOnlyExample("prop8", params -> false, FAILED);
	}

	@Example
	void abortIfNoArbitraryForParameterCanBeFound() {
		List<Parameter> parameters = getParametersForMethod("stringProp");
		CheckedProperty checkedProperty = new CheckedProperty(
				"stringProp",
				params -> false, //never gets theree
				parameters,
				p -> Optional.empty()
		);

		TestExecutionResult check = checkedProperty.check();
		assertThat(check.getStatus()).isEqualTo(TestExecutionResult.Status.ABORTED);
		CannotFindArbitraryException cannotFindeArbitraryException = (CannotFindArbitraryException) check.getThrowable().get();
		assertThat(cannotFindeArbitraryException.getParameter()).isSameAs(parameters.get(0));
	}

	private void intOnlyExample(String methodName, CheckedFunction checkedFunction, TestExecutionResult.Status successful) {
		CheckedProperty checkedProperty = new CheckedProperty(
				methodName,
				checkedFunction,
				getParametersForMethod(methodName),
				p -> Optional.of(new GenericArbitrary(Arbitrary.integer()))
		);
		TestExecutionResult check = checkedProperty.check();
		assertThat(check.getStatus()).isEqualTo(successful);
	}

	private List<Parameter> getParametersForMethod(String methodName) {
		return getParametersFor(Examples.class, methodName);
	}

	private static class Examples {

		public boolean stringProp(@ForAll String aString) {
			return true;
		}

		public boolean prop1(@ForAll int n1) {
			return true;
		}

		public boolean prop2(@ForAll int n1, @ForAll int n2) {
			return true;
		}

		public boolean prop3(@ForAll int n1, @ForAll int n2, @ForAll int n3) {
			return true;
		}

		public boolean prop4(@ForAll int n1, @ForAll int n2, @ForAll int n3, @ForAll int n4) {
			return true;
		}

		public boolean prop5(@ForAll int n1, @ForAll int n2, @ForAll int n3, @ForAll int n4, @ForAll int n5) {
			return true;
		}

		public boolean prop6(@ForAll int n1, @ForAll int n2, @ForAll int n3, @ForAll int n4, @ForAll int n5, @ForAll int n6) {
			return true;
		}

		public boolean prop7(@ForAll int n1, @ForAll int n2, @ForAll int n3, @ForAll int n4, @ForAll int n5, @ForAll int n6, @ForAll int n7) {
			return true;
		}

		public boolean prop8(@ForAll int n1, @ForAll int n2, @ForAll int n3, @ForAll int n4, @ForAll int n5, @ForAll int n6, @ForAll int n7, @ForAll int n8) {
			return true;
		}
	}
}
