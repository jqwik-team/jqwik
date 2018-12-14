package net.jqwik.docs;

import net.jqwik.api.*;

class SelfMadeAnnotationExamples {

	@Property(tries = 10) @Report(Reporting.GENERATED)
	void aGermanText(@ForAll @GermanText String aText) {}
}
