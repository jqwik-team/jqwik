package net.jqwik.execution;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.properties.*;
import net.jqwik.support.*;
import org.junit.platform.engine.reporting.*;
import org.opentest4j.*;

import java.util.*;
import java.util.function.*;

import static net.jqwik.TestHelper.*;
import static net.jqwik.properties.PropertyCheckResult.Status.*;
import static org.assertj.core.api.Assertions.*;

@Group
class CheckedPropertyTests {

	private static final Consumer<ReportEntry> NULL_PUBLISHER = entry -> {
	};

	@Group
	class CheckedPropertyCreation {
		@Example
		void createCheckedPropertyWithoutParameters() {
			PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) TestDescriptorBuilder
					.forMethod(BooleanReturningExamples.class, "propertyWithoutParameters", int.class).build();
			CheckedPropertyFactory factory = new CheckedPropertyFactory();
			CheckedProperty checkedProperty = factory.fromDescriptor(descriptor, new Object());

			assertThat(checkedProperty.configuration.getStereotype()).isEqualTo(Property.DEFAULT_STEREOTYPE);
			assertThat(checkedProperty.configuration.getTries()).isEqualTo(TestDescriptorBuilder.TRIES);
			assertThat(checkedProperty.configuration.getMaxDiscardRatio()).isEqualTo(TestDescriptorBuilder.MAX_DISCARD_RATIO);
			assertThat(checkedProperty.configuration.getShrinkingMode()).isEqualTo(ShrinkingMode.FULL);
			assertThat(checkedProperty.configuration.getReporting()).isEqualTo(new Reporting[0]);
		}

		@Example
		void createCheckedPropertyWithTriesParameter() {
			PropertyMethodDescriptor descriptor = (PropertyMethodDescriptor) TestDescriptorBuilder
					.forMethod(BooleanReturningExamples.class, "propertyWith42TriesAndMaxDiscardRatio2", int.class).build();
			CheckedPropertyFactory factory = new CheckedPropertyFactory();
			CheckedProperty checkedProperty = factory.fromDescriptor(descriptor, new Object());

			assertThat(checkedProperty.configuration.getStereotype()).isEqualTo("OtherStereotype");
			assertThat(checkedProperty.configuration.getTries()).isEqualTo(42);
			assertThat(checkedProperty.configuration.getMaxDiscardRatio()).isEqualTo(2);
			assertThat(checkedProperty.configuration.getShrinkingMode()).isEqualTo(ShrinkingMode.OFF);
			assertThat(checkedProperty.configuration.getReporting()).containsExactly(Reporting.GENERATED);
		}
	}

	@Group
	class PropertiesReturningBoolean {
		@Example
		void intParametersSuccess() {
			intOnlyExample("prop0", params -> params.size() == 0, SATISFIED);
			intOnlyExample("prop1", params -> params.size() == 1, SATISFIED);
			intOnlyExample("prop2", params -> params.size() == 2, SATISFIED);
			intOnlyExample("prop8", params -> params.size() == 8, SATISFIED);
		}

		@Example
		void intParametersFailure() {
			intOnlyExample("prop0", params -> false, FALSIFIED);
			intOnlyExample("prop1", params -> false, FALSIFIED);
			intOnlyExample("prop2", params -> false, FALSIFIED);
			intOnlyExample("prop8", params -> false, FALSIFIED);
		}

		@Example
		void exceptionDuringCheck() {
			RuntimeException toThrow = new RuntimeException("test");
			intOnlyExample("prop0", params -> {
				throw toThrow;
			}, ERRONEOUS);
			intOnlyExample("prop8", params -> {
				throw toThrow;
			}, ERRONEOUS);
		}

		@Example
		void testAbortMakesCheckExhausted() {
			RuntimeException toThrow = new TestAbortedException("test");
			intOnlyExample("prop0", params -> {
				throw toThrow;
			}, EXHAUSTED);
			intOnlyExample("prop8", params -> {
				throw toThrow;
			}, EXHAUSTED);
		}

		@Example
		void assertionErrorMakesCheckFalsified() {
			AssertionError toThrow = new AssertionError("test");
			intOnlyExample("prop0", params -> {
				throw toThrow;
			}, FALSIFIED);
			intOnlyExample("prop8", params -> {
				throw toThrow;
			}, FALSIFIED);
		}

		@Example
		void ifNoArbitraryForParameterCanBeFound_checkIsErroneous() {
			List<MethodParameter> parameters = getParametersForMethod("stringProp");
			CheckedProperty checkedProperty = new CheckedProperty("stringProp", params -> false, parameters, //
					p -> Optional.empty(), //
					new PropertyConfiguration("Property", "1000", 100, 5, ShrinkingMode.FULL, new Reporting[0]));

			PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER);
			assertThat(check.status()).isEqualTo(PropertyCheckResult.Status.ERRONEOUS);
			assertThat(check.throwable()).isPresent();
			assertThat(check.throwable().get()).isInstanceOf(CannotFindArbitraryException.class);
		}

		@Example
		void usingASeedWillAlwaysProvideSameArbitraryValues() {
			List<Integer> allGeneratedInts = new ArrayList<>();
			CheckedFunction addIntToList = params -> allGeneratedInts.add((int) params.get(0));
			CheckedProperty checkedProperty = new CheckedProperty("prop1", addIntToList, getParametersForMethod("prop1"),
																  p -> Optional.of(new GenericArbitrary(Arbitraries.integers()
																												   .between(-100, 100))),
																  new PropertyConfiguration("Property", "42", 12, 5, ShrinkingMode.FULL, new Reporting[0]));

			PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER);
			assertThat(check.randomSeed()).isEqualTo("42");

			assertThat(check.status()).isEqualTo(SATISFIED);
			assertThat(allGeneratedInts).containsExactly(0, 1, -1, -100, 100, 43, -56, -75, 13, 3, 49, -28);
		}

	}

	private void intOnlyExample(String methodName, CheckedFunction forAllFunction, PropertyCheckResult.Status expectedStatus) {
		CheckedProperty checkedProperty = new CheckedProperty(methodName, forAllFunction, getParametersForMethod(methodName),
															  p -> Optional.of(new GenericArbitrary(Arbitraries.integers()
																											   .between(-50, 50))), //
															  new PropertyConfiguration("Property", "1000", 100, 5, ShrinkingMode.FULL, new Reporting[0]));
		PropertyCheckResult check = checkedProperty.check(NULL_PUBLISHER);
		assertThat(check.status()).isEqualTo(expectedStatus);
	}

	private List<MethodParameter> getParametersForMethod(String methodName) {
		return getParametersFor(BooleanReturningExamples.class, methodName);
	}

	private static class BooleanReturningExamples {

		@Property
		public boolean propertyWithoutParameters(@ForAll int anyNumber) {
			return true;
		}

		@Property(stereotype = "OtherStereotype", tries = 42, maxDiscardRatio = 2, shrinking = ShrinkingMode.OFF, reporting = Reporting.GENERATED)
		public boolean propertyWith42TriesAndMaxDiscardRatio2(@ForAll int anyNumber) {
			return true;
		}

		public boolean stringProp(@ForAll String aString) {
			return true;
		}

		public boolean prop0() {
			return true;
		}

		public boolean prop1(@ForAll int n1) {
			return true;
		}

		public boolean prop2(@ForAll int n1, @ForAll int n2) {
			return true;
		}

		public boolean prop8(@ForAll int n1, @ForAll int n2, @ForAll int n3, @ForAll int n4, @ForAll int n5, @ForAll int n6, @ForAll int n7,
				@ForAll int n8) {
			return true;
		}
	}
}
