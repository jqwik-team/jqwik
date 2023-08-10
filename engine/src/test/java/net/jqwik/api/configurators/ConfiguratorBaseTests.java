package net.jqwik.api.configurators;

import java.lang.annotation.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;
import net.jqwik.engine.support.types.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.engine.TestHelper.*;

@PropertyDefaults(tries = 10)
public class ConfiguratorBaseTests {

	@Target({ ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnlyEven {}

	static class OnlyEvenConfigurator extends ArbitraryConfiguratorBase {
		public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, OnlyEven odd) {
			return arbitrary.filter(number  -> Math.abs(number % 2) == 0);
		}
	}

	void propertyWithOnlyEven(@ForAll @OnlyEven int anEvenNumber) {}

	@Example
	void configureMatchingTypeAndAnnotation(@ForAll Random random) {
		OnlyEvenConfigurator configurator = new OnlyEvenConfigurator();

		List<MethodParameter> parameters = getParametersFor(ConfiguratorBaseTests.class, "propertyWithOnlyEven");
		TypeUsage anEvenNumberType = TypeUsageImpl.forParameter(parameters.get(0));

		IntegerArbitrary numbers = Arbitraries.integers();
		Arbitrary<Integer> configuredArbitrary = configurator.configure(numbers, anEvenNumberType);
		assertThat(configuredArbitrary).isNotSameAs(numbers);
		TestingSupport.assertAllGenerated(
			configuredArbitrary, random,
			number -> assertThat(number).isEven()
		);
	}

	@Example
	void configureWithFilteredArbitrary(@ForAll Random random) {
		OnlyEvenConfigurator configurator = new OnlyEvenConfigurator();

		List<MethodParameter> parameters = getParametersFor(ConfiguratorBaseTests.class, "propertyWithOnlyEven");
		TypeUsage anEvenNumberType = TypeUsageImpl.forParameter(parameters.get(0));

		Arbitrary<Integer> numbers = Arbitraries.integers().filter(number -> number % 5 == 0);
		Arbitrary<Integer> configuredArbitrary = configurator.configure(numbers, anEvenNumberType);
		assertThat(configuredArbitrary).isNotSameAs(numbers);
		TestingSupport.assertAllGenerated(
			configuredArbitrary, random,
			number -> assertThat(number % 10).isZero()
		);
	}

	void propertyWithJustInt(@ForAll int aNumber) {}

	@Example
	void dontConfigureMatchingTypeButNonMatchingAnnotation() {
		OnlyEvenConfigurator configurator = new OnlyEvenConfigurator();

		IntegerArbitrary numbers = Arbitraries.integers();
		List<MethodParameter> parameters = getParametersFor(ConfiguratorBaseTests.class, "propertyWithJustInt");
		TypeUsage aNumberType = TypeUsageImpl.forParameter(parameters.get(0));
		assertThat(configurator.configure(numbers, aNumberType)).isSameAs(numbers);
	}

	void propertyWithOnlyEvenString(@ForAll @OnlyEven String aString) {}

	@Example
	void dontConfigureNonMatchingType() {
		OnlyEvenConfigurator configurator = new OnlyEvenConfigurator();

		StringArbitrary strings = Arbitraries.strings();
		List<MethodParameter> parameters = getParametersFor(ConfiguratorBaseTests.class, "propertyWithOnlyEvenString");
		TypeUsage onlyEvenStringType = TypeUsageImpl.forParameter(parameters.get(0));
		assertThat(configurator.configure(strings, onlyEvenStringType)).isSameAs(strings);
	}

	@Example
	@Disabled("Must be fixed")
	void dontConfigureFilteredButNonMatchingType() {
		OnlyEvenConfigurator configurator = new OnlyEvenConfigurator();

		Arbitrary<String> filteredStrings = Arbitraries.strings().filter(s -> s.length() > 5);
		List<MethodParameter> parameters = getParametersFor(ConfiguratorBaseTests.class, "propertyWithOnlyEvenString");
		TypeUsage onlyEvenStringType = TypeUsageImpl.forParameter(parameters.get(0));
		assertThat(configurator.configure(filteredStrings, onlyEvenStringType)).isSameAs(filteredStrings);
	}

	@Target({ ElementType.PARAMETER })
	@Retention(RetentionPolicy.RUNTIME)
	@interface OnlyOdd {}


	static class OnlyConfigurator extends ArbitraryConfiguratorBase {
		public Arbitrary<Integer> configureOnlyEven(Arbitrary<Integer> arbitrary, OnlyEven odd) {
			return arbitrary.filter(number  -> Math.abs(number % 2) == 0);
		}
		public Arbitrary<Integer> configureOnlyOdd(Arbitrary<Integer> arbitrary, OnlyOdd odd) {
			return arbitrary.filter(number  -> Math.abs(number % 2) != 0);
		}

		// Non-public configuration methods are ignored
		private Arbitrary<Integer> configureToZero(Arbitrary<Integer> arbitrary, OnlyOdd odd) {
			return Arbitraries.just(0);
		}
	}

	void propertyOnlyEven(@ForAll @OnlyEven int anEvenNumber) {}
	void propertyOnlyOdd(@ForAll @OnlyOdd int anOddNumber) {}

	@Example
	void configuratorWithExtendedNames(@ForAll Random random) {
		OnlyConfigurator configurator = new OnlyConfigurator();
		IntegerArbitrary integers = Arbitraries.integers();

		List<MethodParameter> paramsOnlyEven = getParametersFor(ConfiguratorBaseTests.class, "propertyOnlyEven");
		TypeUsage anEvenNumberType = TypeUsageImpl.forParameter(paramsOnlyEven.get(0));
		Arbitrary<Integer> configuredEvenArbitrary = configurator.configure(integers, anEvenNumberType);
		assertThat(configuredEvenArbitrary).isNotSameAs(integers);
		TestingSupport.assertAllGenerated(
			configuredEvenArbitrary, random,
			number -> assertThat(number).isEven()
		);

		List<MethodParameter> paramsOnlyOdd = getParametersFor(ConfiguratorBaseTests.class, "propertyOnlyOdd");
		TypeUsage anOddNumberType = TypeUsageImpl.forParameter(paramsOnlyOdd.get(0));
		Arbitrary<Integer> configuredOddArbitrary = configurator.configure(integers, anOddNumberType);
		assertThat(configuredOddArbitrary).isNotSameAs(integers);
		TestingSupport.assertAllGenerated(
			configuredOddArbitrary, random,
			number -> assertThat(number).isOdd()
		);
	}

}
