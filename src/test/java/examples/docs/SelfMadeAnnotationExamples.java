package examples.docs;

import net.jqwik.api.*;

class SelfMadeAnnotationExamples {

	@Property(tries = 10, reporting = Reporting.GENERATED)
	void aGermanText(@ForAll @GermanText String aText) {}
}
