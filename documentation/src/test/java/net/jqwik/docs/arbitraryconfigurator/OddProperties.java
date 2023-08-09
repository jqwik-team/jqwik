package net.jqwik.docs.arbitraryconfigurator;

import java.math.*;

import net.jqwik.api.*;

class OddProperties {

	@Property @Report(Reporting.GENERATED)
	boolean oddIntegersOnly(@ForAll @Odd int aNumber) {
		return Math.abs(aNumber % 2) == 1;
	}

	@Property @Report(Reporting.GENERATED)
	boolean oddBigIntegersOnly(@ForAll @Odd BigInteger aNumber) {
		return Math.abs(aNumber.longValueExact() % 2) == 1;
	}
}
