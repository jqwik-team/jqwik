package examples.packageWithSingleContainer;

import static org.assertj.core.api.Assertions.*;

import net.jqwik.api.*;

public class SimpleExampleTests {

	@Example
	void succeeding() {
	}

	@Example
	static void staticExample() {
	}

	@Example
	void failing() {
		fail("failing");
	}
}
