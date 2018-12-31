package examples.packageWithSingleContainer;

import net.jqwik.api.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleExampleTests {

	@Property
	void succeeding() {
	}

	@Property
	static void staticExample() {
	}

	@Property
	@DisplayName("with Jupiter annotation")
	void withJupiterAnnotation() {
	}

	@Property
	void failing() {
		fail("failing");
	}
}
