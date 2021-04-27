package examples.packageWithInheritance;

import net.jqwik.api.*;

public abstract class AbstractContainer {

	@Example
	void exampleToInherit() {
	}

	@Example
	void exampleToOverride() {
	}

	@Example
	void exampleToDisable() {
	}

	@Group
	public class ContainerInAbstractClass {
		@Example
		void innerExampleToInherit() {

		}
	}
}
