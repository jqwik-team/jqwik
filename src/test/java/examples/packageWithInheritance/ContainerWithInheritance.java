package examples.packageWithInheritance;

import net.jqwik.api.*;

public class ContainerWithInheritance extends AbstractContainer implements InterfaceTests {

	@Example
	void example() {}

	@Example
	@Override
	void exampleToOverride() { }

	@Example
	@Override
	public void exampleToOverrideFromInterface() { }

	@Override
	void exampleToDisable() { }

}
