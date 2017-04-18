package net.jqwik.execution.properties;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.properties.*;
import net.jqwik.descriptor.*;
import net.jqwik.properties.*;
import net.jqwik.properties.arbitraries.*;
import org.junit.platform.engine.*;

import java.lang.reflect.*;
import java.util.*;

import static net.jqwik.execution.properties.TestHelper.*;
import static net.jqwik.properties.PropertyCheckResult.Status.*;
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

			assertThat(checkedProperty.getTries()).isEqualTo(1000);
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
	void intParametersSuccess() {
		intOnlyExample("prop1", params -> params.size() == 1, SATISFIED);
		intOnlyExample("prop2", params -> params.size() == 2, SATISFIED);
		intOnlyExample("prop3", params -> params.size() == 3, SATISFIED);
		intOnlyExample("prop4", params -> params.size() == 4, SATISFIED);
		intOnlyExample("prop5", params -> params.size() == 5, SATISFIED);
		intOnlyExample("prop6", params -> params.size() == 6, SATISFIED);
		intOnlyExample("prop7", params -> params.size() == 7, SATISFIED);
		intOnlyExample("prop8", params -> params.size() == 8, SATISFIED);
	}

	@Example
	void intParametersFailure() {
		intOnlyExample("prop1", params -> false, FALSIFIED);
		intOnlyExample("prop2", params -> false, FALSIFIED);
		intOnlyExample("prop3", params -> false, FALSIFIED);
		intOnlyExample("prop4", params -> false, FALSIFIED);
		intOnlyExample("prop5", params -> false, FALSIFIED);
		intOnlyExample("prop6", params -> false, FALSIFIED);
		intOnlyExample("prop7", params -> false, FALSIFIED);
		intOnlyExample("prop8", params -> false, FALSIFIED);
	}

	@Example
	void abortIfNoArbitraryForParameterCanBeFound() {
		List<Parameter> parameters = getParametersForMethod("stringProp");
		CheckedProperty checkedProperty = new CheckedProperty("stringProp", params -> false,
															  parameters, p -> Optional.empty(), 100, 1000L);

		PropertyCheckResult check = checkedProperty.check();
		assertThat(check.status()).isEqualTo(PropertyCheckResult.Status.ERRONEOUS);
		CannotFindArbitraryException cannotFindeArbitraryException = (CannotFindArbitraryException) check.throwable().get();
		assertThat(cannotFindeArbitraryException.getParameter()).isSameAs(parameters.get(0));
	}

	@Example
	void usingASeedWillAlwaysProvideSameArbitraryValues() {
		List<Integer> allGeneratedInts = new ArrayList<>();
		CheckedFunction addIntToList = params -> allGeneratedInts.add((int) params.get(0));
		CheckedProperty checkedProperty = new CheckedProperty("prop1", addIntToList, getParametersForMethod("prop1"),
			p -> Optional.of(new GenericArbitrary(Arbitraries.integer(-100, 100))), 10, 42L);

		PropertyCheckResult check = checkedProperty.check();
		assertThat(check.randomSeed()).isEqualTo(42L);

		assertThat(check.status()).isEqualTo(SATISFIED);
		assertThat(allGeneratedInts).containsExactly(-59, 20, -10, 1, -88, -87, 100, 40, 96, 82);
	}

	private void intOnlyExample(String methodName, CheckedFunction forAllFunction, PropertyCheckResult.Status expectedStatus) {
		CheckedProperty checkedProperty = new CheckedProperty(methodName, forAllFunction, getParametersForMethod(methodName),
			p -> Optional.of(new GenericArbitrary(Arbitraries.integer(-50, 50))), 100, 1000L);
		PropertyCheckResult check = checkedProperty.check();
		assertThat(check.status()).isEqualTo(expectedStatus);
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
