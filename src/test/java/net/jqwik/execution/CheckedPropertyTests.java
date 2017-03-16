package net.jqwik.execution;

import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import org.assertj.core.api.Assertions;
import org.junit.platform.engine.TestExecutionResult;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.platform.engine.TestExecutionResult.Status.FAILED;
import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;

class CheckedPropertyTests {

	@Example
	void intParametersSuccess() {
		propertyExample("prop1", params -> params.length == 1, SUCCESSFUL);
		propertyExample("prop2", params -> params.length == 2, SUCCESSFUL);
		propertyExample("prop3", params -> params.length == 3, SUCCESSFUL);
		propertyExample("prop4", params -> params.length == 4, SUCCESSFUL);
		propertyExample("prop5", params -> params.length == 5, SUCCESSFUL);
//		propertyExample("prop6", params -> params.length == 6, SUCCESSFUL);
//		propertyExample("prop7", params -> params.length == 7, SUCCESSFUL);
//		propertyExample("prop8", params -> params.length == 8, SUCCESSFUL);
	}

	@Example
	void intParametersFailure() {
		propertyExample("prop1", params -> false, FAILED);
		propertyExample("prop2", params -> false, FAILED);
		propertyExample("prop3", params -> false, FAILED);
		propertyExample("prop4", params -> false, FAILED);
		propertyExample("prop5", params -> false, FAILED);
//		propertyExample("prop6", params -> false, FAILED);
//		propertyExample("prop7", params -> false, FAILED);
//		propertyExample("prop8", params -> false, FAILED);
	}

	private void propertyExample(String methodName, CheckedFunction checkedFunction, TestExecutionResult.Status successful) {
		CheckedProperty checkedProperty = new CheckedProperty(
				methodName,
				checkedFunction,
				getParametersForMethod(methodName)
		);
		TestExecutionResult check = checkedProperty.check();
		Assertions.assertThat(check.getStatus()).isEqualTo(successful);
	}

	private List<Parameter> getParametersForMethod(String methodName) {
		return getParameters(getMethod(methodName));
	}

	private List<Parameter> getParameters(Method method) {
		return Arrays.stream(method.getParameters()).collect(Collectors.toList());
	}

	private Method getMethod(String methodName) {
		return Arrays.stream(Examples.class.getMethods()).filter(m -> m.getName().equals(methodName)).findFirst().get();
	}

	private static class Examples {
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
