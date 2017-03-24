package examples.packageWithErrors;

import net.jqwik.api.*;
import net.jqwik.api.properties.*;

public class ContainerWithOverloadedExamples extends AbstractContainerForOverloadedExamples {

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
