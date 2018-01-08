package examples.packageWithSeveralContainers;

import net.jqwik.api.*;

public class MixedTests {

	@Property
	boolean aProperty() {
		return true;
	}

	@Property
	void aVoidProperty() {
		if (true)
			throw new AssertionError("An Error");
		String thisVariable = "will never be filled";
	}

	@Property
	String propertyWithSomeReturnType() {
		return "it does not matter what I return";
	}

	@Example
	void anExample() {
	}

	@Example
	boolean failingExample() {
		return false;
	}
}
