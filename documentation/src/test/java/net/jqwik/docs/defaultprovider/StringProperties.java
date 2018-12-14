package net.jqwik.docs.defaultprovider;

import net.jqwik.api.*;

class StringProperties {

	@Property @Report(Reporting.GENERATED)
	void aString(@ForAll String aString) {}
}
