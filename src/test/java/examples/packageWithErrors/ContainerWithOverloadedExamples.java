package examples.packageWithErrors;

import net.jqwik.api.Example;
import net.jqwik.api.properties.Property;

public class ContainerWithOverloadedExamples extends AbstractContainerForOverloadedExamples{

	@Example
	void succeeding() {
	}

	@Example
	void overloadedExample() {
	}


	@Example
	void overloadedExample(int aNumber) {
	}

	@Property
	boolean overloadedProperty(int aNumber) {
		return true;
	}

	@Property
	boolean overloadedProperty(String aString) {
		return true;
	}
}
