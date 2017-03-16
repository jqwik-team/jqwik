package examples.packageWithSeveralContainers;

import net.jqwik.api.Property;

public class PropertyTests {

	@Property
	boolean isTrue() {
		return true;
	}

	@Property
	Boolean isAlsoTrue() {
		return Boolean.TRUE;
	}

	@Property
	boolean isFalse() {
		return false;
	}

	@Property
	String incompatibleReturnType() {
		return "aString";
	}
}
