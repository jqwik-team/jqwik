package examples.docs.defaultprovider;

import net.jqwik.api.*;

class StringProperties {

	@Property(reporting = Reporting.GENERATED)
	void aString(@ForAll String aString) {}
}
