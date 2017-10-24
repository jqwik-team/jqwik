package examples.docs;

import net.jqwik.api.*;
import org.assertj.core.data.*;

import static org.assertj.core.api.Assertions.*;

class ExampleBasedTests {

	@Example
	void squareRootOf16is4() {
		assertThat(Math.sqrt(16)).isCloseTo(4.0, Offset.offset(0.01));
	}

	@Example
	boolean add1plu3is4() {
		return (1 + 3) == 4;
	}
}
