package examples.packageWithErrors;

import net.jqwik.api.Example;
import net.jqwik.api.properties.Property;

public class ContainerWithStaticTestMethods {
	@Example
	static void staticExampleShouldBeSkipped() {
	}

	@Property
	static boolean staticPropertyShouldBeSkipped() {
		return true;
	}

}
