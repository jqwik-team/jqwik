package examples.packageWithSeveralContainers;

import static org.assertj.core.api.Assertions.fail;

import net.jqwik.api.Example;

public class ExampleTests {

	@Example
	void succeeding() {

	}

	@Example
	void failing() {
		fail("failing");
	}
}
