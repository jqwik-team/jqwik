package examples.packageWithInheritance;

import net.jqwik.api.*;

public interface InterfaceTests {
	@Example
	default void exampleToInheritFromInterface() {
	}

	@Example
	default void exampleToOverrideFromInterface() {
	}
}
