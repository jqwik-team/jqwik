package examples.packageWithSeveralContainers;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class ExampleTests {

	@Example
	void succeeding() {

	}

	@Example
	void failing() {
		fail("failing");
	}
}
