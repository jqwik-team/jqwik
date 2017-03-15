package examples.packageWithSeveralContainers;

import net.jqwik.api.Example;
import net.jqwik.api.Property;

public class MixedTests {

	@Property
	boolean aProperty() {
		return true;
	}

	@Example
	void anExample() {
	}
}
