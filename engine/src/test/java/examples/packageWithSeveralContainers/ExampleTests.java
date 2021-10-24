package examples.packageWithSeveralContainers;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class ExampleTests {

	@Example
	void succeeding() {
	}

	@Example
	String succeedingWithStringAsObject() {
		return "hallo";
	}

	@Example
	void failingSimple() {
		fail("failing");
	}

	@Example
	Object failingWithFalseAsObject() {
		return false;
	}
}
