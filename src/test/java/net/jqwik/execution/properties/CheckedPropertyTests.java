package net.jqwik.execution.properties;

import javaslang.test.*;
import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.properties.*;
import net.jqwik.api.properties.Property;
import net.jqwik.descriptor.*;
import org.junit.platform.engine.*;

import java.lang.reflect.*;
import java.util.*;

import static net.jqwik.execution.properties.TestHelper.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.platform.engine.TestExecutionResult.Status.*;

class CheckedPropertyTests {

	@Group
	class CheckedPropertyCreation {
		@Example
		void createCheckedPropertyWithoutTriesParameter() throws NoSuchMethodException {
			PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) TestDescriptorBuilder
				.forMethod(Examples.class, "propertyWithoutTries", int.class).build();
			CheckedPropertyFactory factory = new CheckedPropertyFactory();
			CheckedProperty checkedProperty = factory.fromDescriptor(descriptor, new Object());

			assertThat(checkedProperty.getTries()).isEqualTo(Checkable.DEFAULT_TRIES);
		}

		@Example
		void createCheckedPropertyWithTriesParameter() throws NoSuchMethodException {
			PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) TestDescriptorBuilder
				.forMethod(Examples.class, "propertyWith42Tries", int.class).build();
			CheckedPropertyFactory factory = new CheckedPropertyFactory();
			CheckedProperty checkedProperty = factory.fromDescriptor(descriptor, new Object());

			assertThat(checkedProperty.getTries()).isEqualTo(42);
		}
	}

	@Example
	void assumptionFailureIsASuccessOfProperty() {
		intOnlyExample("prop1", params -> false, params -> false, SUCCESSFUL);
		intOnlyExample("prop2", params -> false, params -> false, SUCCESSFUL);
		intOnlyExample("prop3", params -> false, params -> false, SUCCESSFUL);
		intOnlyExample("prop4", params -> false, params -> false, SUCCESSFUL);
		intOnlyExample("prop5", params -> false, params -> false, SUCCESSFUL);
		intOnlyExample("prop6", params -> false, params -> false, SUCCESSFUL);
		intOnlyExample("prop7", params -> false, params -> false, SUCCESSFUL);
		intOnlyExample("prop8", params -> false, params -> false, SUCCESSFUL);
	}

	@Example
	void intParametersSuccess() {
		intOnlyExample("prop1", params -> true, params -> params.length == 1, SUCCESSFUL);
		intOnlyExample("prop2", params -> true, params -> params.length == 2, SUCCESSFUL);
		intOnlyExample("prop3", params -> true, params -> params.length == 3, SUCCESSFUL);
		intOnlyExample("prop4", params -> true, params -> params.length == 4, SUCCESSFUL);
		intOnlyExample("prop5", params -> true, params -> params.length == 5, SUCCESSFUL);
		intOnlyExample("prop6", params -> true, params -> params.length == 6, SUCCESSFUL);
		intOnlyExample("prop7", params -> true, params -> params.length == 7, SUCCESSFUL);
		intOnlyExample("prop8", params -> true, params -> params.length == 8, SUCCESSFUL);
	}

	@Example
	void intParametersFailure() {
		intOnlyExample("prop1", params -> true, params -> false, FAILED);
		intOnlyExample("prop2", params -> true, params -> false, FAILED);
		intOnlyExample("prop3", params -> true, params -> false, FAILED);
		intOnlyExample("prop4", params -> true, params -> false, FAILED);
		intOnlyExample("prop5", params -> true, params -> false, FAILED);
		intOnlyExample("prop6", params -> true, params -> false, FAILED);
		intOnlyExample("prop7", params -> true, params -> false, FAILED);
		intOnlyExample("prop8", params -> true, params -> false, FAILED);
	}

	@Example
	void abortIfNoArbitraryForParameterCanBeFound() {
		List<Parameter> parameters = getParametersForMethod("stringProp");
		CheckedProperty checkedProperty = new ExecutingCheckedProperty("stringProp", params -> true, params -> false,
																	   parameters, p -> Optional.empty(), 100, 1000L);

		TestExecutionResult check = checkedProperty.check().getTestExecutionResult();
		assertThat(check.getStatus()).isEqualTo(TestExecutionResult.Status.ABORTED);
		CannotFindArbitraryException cannotFindeArbitraryException = (CannotFindArbitraryException) check.getThrowable().get();
		assertThat(cannotFindeArbitraryException.getParameter()).isSameAs(parameters.get(0));
	}

	@Example
	void usingASeedWillAlwaysProvideSameArbitraryValues() {
		List<Integer> allGeneratedInts = new ArrayList<>();
		CheckedFunction addIntToList = params -> allGeneratedInts.add((int) params[0]);
		CheckedProperty checkedProperty = new ExecutingCheckedProperty("prop1", params -> true, addIntToList, getParametersForMethod("prop1"),
			p -> Optional.of(new GenericArbitrary(Arbitrary.integer(), Checkable.DEFAULT_SIZE)), 10, 42L);

		PropertyExecutionResult executionResult = checkedProperty.check();
		assertThat(executionResult.getSeed()).isEqualTo(42L);

		TestExecutionResult check = executionResult.getTestExecutionResult();
		assertThat(check.getStatus()).isEqualTo(SUCCESSFUL);
		assertThat(allGeneratedInts).containsExactly(-59, 20, -10, 1, -88, -87, 100, 40, 96, 82);
	}

	private void intOnlyExample(String methodName, CheckedFunction assumeFunction, CheckedFunction forAllFunction, TestExecutionResult.Status successful) {
		CheckedProperty checkedProperty = new ExecutingCheckedProperty(methodName, assumeFunction, forAllFunction, getParametersForMethod(methodName),
			p -> Optional.of(new GenericArbitrary(Arbitrary.integer(), Checkable.DEFAULT_SIZE)), 100, 1000L);
		TestExecutionResult check = checkedProperty.check().getTestExecutionResult();
		assertThat(check.getStatus()).isEqualTo(successful);
	}

	private List<Parameter> getParametersForMethod(String methodName) {
		return getParametersFor(Examples.class, methodName);
	}

	private static class Examples {

		@Property
		public boolean propertyWithoutTries(@ForAll int anyNumber) {
			return true;
		}

		@Property(tries = 42)
		public boolean propertyWith42Tries(@ForAll int anyNumber) {
			return true;
		}

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

		public boolean prop7(@ForAll int n1, @ForAll int n2, @ForAll int n3, @ForAll int n4, @ForAll int n5, @ForAll int n6,
							 @ForAll int n7) {
			return true;
		}

		public boolean prop8(@ForAll int n1, @ForAll int n2, @ForAll int n3, @ForAll int n4, @ForAll int n5, @ForAll int n6, @ForAll int n7,
							 @ForAll int n8) {
			return true;
		}
	}
}
