package examples.docs;

import net.jqwik.api.*;

@Tag("integration-test")
class TaggingExamples {

	@Property
	@Tag("fast")
	void aFastProperty() { }

	@Example
	@Tag("slow") @Tag("involved")
	void aSlowTest() { }

}
