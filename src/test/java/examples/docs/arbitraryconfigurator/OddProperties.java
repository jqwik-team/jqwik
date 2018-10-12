package examples.docs.arbitraryconfigurator;

import net.jqwik.api.*;

class OddProperties {

	@Property @Report(Reporting.GENERATED)
	boolean oddIntegersOnly(@ForAll @Odd int aNumber) {
		return Math.abs(aNumber % 2) == 1;
	}
}
