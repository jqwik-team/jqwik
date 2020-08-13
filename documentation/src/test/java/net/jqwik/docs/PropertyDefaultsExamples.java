package net.jqwik.docs;

import net.jqwik.api.*;

@PropertyDefaults(tries = 10, shrinking = ShrinkingMode.FULL)
class PropertyDefaultsExamples {

	@Property
	void aLongRunningProperty(@ForAll String aString) {}

	@Property(shrinking = ShrinkingMode.OFF)
	void anotherLongRunningProperty(@ForAll String aString) {}
}
