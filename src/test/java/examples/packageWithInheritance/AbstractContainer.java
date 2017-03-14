package examples.packageWithInheritance;

import net.jqwik.api.Example;

public abstract class AbstractContainer {

	@Example
	void exampleToInherit() { }

	@Example
	void exampleToOverride() { }

	@Example
	void exampleToDisable() { }
}
