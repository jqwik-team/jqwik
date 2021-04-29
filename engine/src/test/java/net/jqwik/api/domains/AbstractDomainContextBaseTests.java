package net.jqwik.api.domains;

import java.lang.annotation.*;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

@Label("AbstractDomainContextBaseTests (deprecated)")
class AbstractDomainContextBaseTests {

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

	@Property(tries = 20)
	@Domain(SmallNumbersContext.class)
	@Domain(value = BigNumbersContext.class, priority = 10)
	void contextDefaultPriorityCanBeOverridden(
		@ForAll int aNumber
	) {
		assertThat(aNumber).isBetween(1000000, 9000000);
	}

	@Property
	@Domain(SpecificNumbersContext.class)
	void contextFromSuperClass(
		@ForAll int aNumber
	) {
		assertThat(aNumber).isBetween(10, 12);
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
			setDefaultPriority(1); // supersede built-in arbitrary providers
			registerArbitrary(Integer.class, Arbitraries.of(1, 2, 3, 41, 55, 97, 98, 99));
			registerConfigurator(new ArbitraryConfiguratorBase() {
				public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, DoubleNumber ignore) {
					return arbitrary.map(i -> 2 * i);
				}
			});
		}
	}

	private static class BigNumbersContext extends AbstractDomainContextBase {
		private BigNumbersContext() {
			registerArbitrary(Integer.class, Arbitraries.of(1000000, 2000000, 3000000));
		}
	}

	private static class SpecificNumbersContext extends AbstractDomainContextBase {
		Integer[] specificNumbers = new Integer[]{10, 11, 12};

		private SpecificNumbersContext() {
			registerArbitrary(Integer.class, Arbitraries.of(specificNumbers));
		}
	}

}
