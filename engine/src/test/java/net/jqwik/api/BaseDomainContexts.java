package net.jqwik.api;

import net.jqwik.api.domains.AbstractDomainContextBase;

abstract class BaseDomainContexts {

	Integer[] specificNumbers;

	class SpecificNumbersContext extends AbstractDomainContextBase {
		private SpecificNumbersContext() {
			registerArbitrary(Integer.class, Arbitraries.of(specificNumbers));
		}
	}

}
