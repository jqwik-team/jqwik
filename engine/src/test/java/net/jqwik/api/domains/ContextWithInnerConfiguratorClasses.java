package net.jqwik.api.domains;

import net.jqwik.api.*;
import net.jqwik.api.configurators.*;

class ContextWithInnerConfiguratorClasses extends DomainContextBase {

	class DoubleStringConfigurator extends ArbitraryConfiguratorBase {
		public Arbitrary<String> configure(Arbitrary<String> arbitrary, Doubled ignore) {
			return arbitrary.map(s -> s + s);
		}
	}

	static class NegateIntConfigurator extends ArbitraryConfiguratorBase {
		public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, MakeNegative ignore) {
			return arbitrary.map(i -> {
				if (i <= 0) {
					return i;
				}
				return Math.negateExact(i);
			});
		}
	}

	private class ShouldNotBeUsedBecausePrivate extends ArbitraryConfiguratorBase {
		public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, MakeNegative ignore) {
			return arbitrary.map(i -> Math.abs(i));
		}
	}

}
