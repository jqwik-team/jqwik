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
	void configuratorWithMatchingAnnotation(@ForAll Random random) {
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

	void propertyWithJustInt(@ForAll int aNumber) {}

	@Example
	void configuratorWithNonMatchingAnnotation() {
		OnlyEvenConfigurator configurator = new OnlyEvenConfigurator();

		IntegerArbitrary numbers = Arbitraries.integers();
		List<MethodParameter> parameters = getParametersFor(ConfiguratorBaseTests.class, "propertyWithJustInt");
		TypeUsage aNumberType = TypeUsageImpl.forParameter(parameters.get(0));
		assertThat(configurator.configure(numbers, aNumberType)).isSameAs(numbers);
	}

	void propertyWithOnlyEvenString(@ForAll @OnlyEven String aString) {}

	// @Example
	// TODO: The actual arbitrary instance must be matched against the configuration methods first parameter
	void configuratorWithNonMatchingType() {
		OnlyEvenConfigurator configurator = new OnlyEvenConfigurator();

		StringArbitrary strings = Arbitraries.strings();
		List<MethodParameter> parameters = getParametersFor(ConfiguratorBaseTests.class, "propertyWithOnlyEvenString");
		TypeUsage onlyEvenStringType = TypeUsageImpl.forParameter(parameters.get(0));
		assertThat(configurator.configure(strings, onlyEvenStringType)).isSameAs(strings);
	}
}
