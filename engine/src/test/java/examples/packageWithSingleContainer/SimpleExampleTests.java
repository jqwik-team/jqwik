package examples.packageWithSingleContainer;

import org.junit.jupiter.api.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleExampleTests {

	@Example
	void succeeding() {
	}

	@Example
	static void staticExample() {
	}

	@Example
	@DisplayName("with Jupiter annotation")
	void withJupiterAnnotation() {
	}

	@Example
	void failing() {
		fail("failing");
	}
}
