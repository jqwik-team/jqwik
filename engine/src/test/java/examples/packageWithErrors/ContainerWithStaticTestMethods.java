package examples.packageWithErrors;

import net.jqwik.api.*;

public class ContainerWithStaticTestMethods {
	@Example
	static void staticExampleShouldBeSkipped() {
	}

	@Property
	static boolean staticPropertyShouldBeSkipped() {
		return true;
	}

}
