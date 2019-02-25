package net.jqwik.api;

import java.lang.annotation.*;

import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.domains.*;

import static org.assertj.core.api.Assertions.*;

class DomainContextTests {

	@Property(tries = 20)
	@Domain(NumberStringContext.class)
	void onlyUseProvidersFromDeclaredDomain(@ForAll String aString) {
		assertThat(aString).hasSize(2);
		assertThat(aString).containsOnlyDigits();
	}

	@Property(tries = 20)
	@Domain(NumberStringContext.class)
	void applyConfiguratorsFromDeclaredDomain(@ForAll @DoubleString String aString) {
		assertThat(aString).hasSize(4);
		assertThat(aString).containsOnlyDigits();
	}

	@Property(tries = 20)
	@Domain(NumberStringContext.class)
	@Domain(SmallNumbersContext.class)
	void severalDomainsAreConcatenated(
		@ForAll @DoubleString String aString,
		@ForAll int smallNumber,
		@ForAll @DoubleNumber int doubledNumber
	) {
		assertThat(aString).hasSize(4);
		assertThat(aString).containsOnlyDigits();
		assertThat(smallNumber).isBetween(1, 99);
		assertThat(doubledNumber).isBetween(2, 198);
	}

	@Property(tries = 20)
	@Domain(DomainContext.Global.class)
	@Domain(SmallNumbersContext.class)
	void globalContextCanBeMixedIn(
		@ForAll @StringLength(5) @LowerChars String aString,
		@ForAll int smallNumber
	) {
		assertThat(aString).hasSize(5);
		assertThat(aString.chars()).allSatisfy(Character::isLowerCase);
		assertThat(smallNumber).isBetween(1, 99);
	}

	@Target({ElementType.PARAMETER})
	@Retention(RetentionPolicy.RUNTIME)
	@interface DoubleString {}

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
	@interface DoubleNumber {}

	private static class SmallNumbersContext extends AbstractDomainContextBase {
		private SmallNumbersContext() {
			registerArbitrary(Integer.class, Arbitraries.of(1000, 2000, 3000), 1); // should be overridden
			registerArbitrary(Integer.class, Arbitraries.of(1, 2, 3, 41, 55, 97, 98, 99), 10);
			registerConfigurator(new ArbitraryConfiguratorBase() {
				public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, DoubleNumber ignore) {
					return arbitrary.map(i -> 2 * i);
				}
			});
		}
	}
}
