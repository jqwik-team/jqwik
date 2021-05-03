package net.jqwik.api.domains;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

@Group
class DomainContextBaseTests {

	@Group
	class ArbitraryProviders {

		@Property(tries = 20)
		@Domain(NumberStringContext.class)
		void onlyUseProvidersFromDeclaredDomain(@ForAll String aString, @ForAll char aChar) {
			assertThat(aString).hasSize(2);
			assertThat(aString).containsOnlyDigits();
			assertThat(aChar).isBetween('0', '9');
		}

	}

}


class NumberStringContext extends DomainContextBase {

	@Provide
	StringArbitrary numberStrings() {
		return Arbitraries.strings().numeric().ofLength(2);
	}

	@Provide
	Arbitrary<Character> numberChars() {
		return Arbitraries.integers().between(0, 9).map(i -> Character.forDigit(i, 10));
	}

	// private NumberStringContext() {
	// 	registerConfigurator(new ArbitraryConfiguratorBase() {
	// 		public Arbitrary<String> configure(Arbitrary<String> arbitrary, AbstractDomainContextBaseTests.DoubleString ignore) {
	// 			return arbitrary.map(s -> s + s);
	// 		}
	// 	});
	// }
}
