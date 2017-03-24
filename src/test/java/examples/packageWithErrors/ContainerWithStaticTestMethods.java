package examples.packageWithErrors;

import net.jqwik.api.*;
import net.jqwik.api.properties.*;

public class ContainerWithStaticTestMethods {
	@Example
	static void staticExampleShouldBeSkipped() {
	}

	@Property
	static boolean staticPropertyShouldBeSkipped() {
		return true;
	}

}
