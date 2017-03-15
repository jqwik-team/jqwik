package examples.packageWithSeveralContainers;

import net.jqwik.api.Property;

public class PropertyTests {

	@Property
	boolean isTrue() {
		return true;
	}

	@Property
	boolean isFalse() {
		return false;
	}
}
