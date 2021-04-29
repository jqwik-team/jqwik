package net.jqwik.api.domains;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;

import static org.assertj.core.api.Assertions.*;

@Group
class DomainContextBaseTests {

	@Group
	class ArbitraryProviders {

		@Property(tries = 20)
		@Domain(NumberStringContext.class)
		void onlyUseProvidersFromDeclaredDomain(@ForAll String aString) {
			assertThat(aString).hasSize(2);
			assertThat(aString).containsOnlyDigits();
		}

	}

}


class NumberStringContext extends DomainContextBase {

	@Provide
	Arbitrary<String> numberStrings() {
		return Arbitraries.integers().between(10, 99).map(i -> Integer.toString(i));
	}

	// private NumberStringContext() {
	// 	registerConfigurator(new ArbitraryConfiguratorBase() {
	// 		public Arbitrary<String> configure(Arbitrary<String> arbitrary, AbstractDomainContextBaseTests.DoubleString ignore) {
	// 			return arbitrary.map(s -> s + s);
	// 		}
	// 	});
	// }
}
