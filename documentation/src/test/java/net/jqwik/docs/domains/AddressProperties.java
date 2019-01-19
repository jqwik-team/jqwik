package net.jqwik.docs.domains;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;

class AddressProperties {

	@Property
	@Domain(AddressDomain.class)
	@Report(Reporting.GENERATED)
	void anAddressWithAStreetNumber(@ForAll Address anAddress, @ForAll int streetNumber) {
	}

	@Property
	@Domain(AddressDomain.class)
	void globalDomainNotPresent(@ForAll Address anAddress, @ForAll String anyString) {
	}

	@Property
	@Domain(DomainContext.Global.class)
	@Domain(AddressDomain.class)
	void globalDomainCanBeAdded(@ForAll Address anAddress, @ForAll String anyString) {
	}
}
