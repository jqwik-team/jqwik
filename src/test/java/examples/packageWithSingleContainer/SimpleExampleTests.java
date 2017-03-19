package examples.packageWithSingleContainer;

import net.jqwik.api.Example;

import static org.assertj.core.api.Assertions.fail;

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
