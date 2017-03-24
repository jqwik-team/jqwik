package examples.packageWithSeveralContainers;

import static org.assertj.core.api.Assertions.*;

import net.jqwik.api.*;

public class ExampleTests {

	@Example
	void succeeding() {

	}

	@Example
	void failing() {
		fail("failing");
	}
}
