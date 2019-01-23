package net.jqwik.docs.domains;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;

class AddressProperties {

	@Property
	@Domain(AmericanAddresses.class)
	@Report(Reporting.GENERATED)
	void anAddressWithAStreetNumber(@ForAll Address anAddress, @ForAll int streetNumber) {
	}

	@Property
	@Domain(AmericanAddresses.class)
	void globalDomainNotPresent(@ForAll Address anAddress, @ForAll String anyString) {
	}

	@Property
	@Domain(DomainContext.Global.class)
	@Domain(AmericanAddresses.class)
	void globalDomainCanBeAdded(@ForAll Address anAddress, @ForAll String anyString) {
	}
}
