package examples.packageWithErrors;

import net.jqwik.api.Example;

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
}
