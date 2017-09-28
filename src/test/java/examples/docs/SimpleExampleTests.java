package examples.docs;

import static org.assertj.core.api.Assertions.*;

import net.jqwik.api.*;
import org.assertj.core.data.*;

class SimpleExampleTests implements AutoCloseable {
	@Example
	void succeeding() {
		assertThat(Math.sqrt(15)).isCloseTo(3.872, Offset.offset(0.01));
	}

	@Example
	void failing() {
		fail("failing");
	}

	// Executed after each test case
	public void close() { }

	@Group
	class AGroupOfCoherentTests {
		@Example
		void anotherSuccess() { }
	}
}