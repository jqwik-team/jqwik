package examples.packageWithSeveralContainers;

import net.jqwik.api.*;
import net.jqwik.api.properties.*;

public class MixedTests {

	@Property
	boolean aProperty() {
		return true;
	}

	@Example
	void anExample() {
	}
}
