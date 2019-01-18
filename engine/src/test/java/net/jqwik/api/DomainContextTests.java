package net.jqwik.api;

import java.lang.annotation.*;

import org.assertj.core.api.*;

import net.jqwik.api.configurators.*;
import net.jqwik.api.domains.*;

class DomainContextTests {

	@Property(tries = 20)
	@Domain(NumberStringContext.class)
	void onlyUseProvidersFromDeclaredDomain(@ForAll String aString) {
		Assertions.assertThat(aString).hasSize(2);
		Assertions.assertThat(aString).containsOnlyDigits();
	}

	@Property(tries = 20)
	@Domain(NumberStringContext.class)
	void applyConfiguratorsFromDeclaredDomain(@ForAll @DoubleString String aString) {
		Assertions.assertThat(aString).hasSize(4);
		Assertions.assertThat(aString).containsOnlyDigits();
	}

	@Target({ElementType.PARAMETER})
	@Retention(RetentionPolicy.RUNTIME)
	static @interface DoubleString { }

	private static class NumberStringContext extends AbstractDomainContextBase {
		private NumberStringContext() {
			registerArbitrary(String.class, Arbitraries.integers().between(10, 99).map(i -> Integer.toString(i)));
			registerConfigurator(new ArbitraryConfiguratorBase() {
				public Arbitrary<String> configure(Arbitrary<String> arbitrary, DoubleString ignore) {
					return arbitrary.map(s -> s + s);
				}
			});
		}
	}

	@Target({ElementType.PARAMETER})
	@Retention(RetentionPolicy.RUNTIME)
	static @interface DoubleNumber { }


	private static class SmallNumbersContext extends AbstractDomainContextBase {
		private SmallNumbersContext() {
			registerArbitrary(Integer.class, Arbitraries.integers().between(1, 99));
			registerConfigurator(new ArbitraryConfiguratorBase() {
				public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, DoubleNumber ignore) {
					return arbitrary.map(i -> 2 * i);
				}
			});
		}
	}
}
