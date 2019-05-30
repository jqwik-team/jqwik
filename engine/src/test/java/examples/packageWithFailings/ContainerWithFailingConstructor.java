package examples.packageWithFailings;

import net.jqwik.api.*;

public class ContainerWithFailingConstructor {

	public ContainerWithFailingConstructor() {
		throw new RuntimeException("failing constructor");
	}

	@Example
	void success() {}
}
