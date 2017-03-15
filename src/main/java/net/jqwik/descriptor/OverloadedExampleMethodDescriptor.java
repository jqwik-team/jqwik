package net.jqwik.descriptor;

import org.junit.platform.engine.UniqueId;

public class OverloadedExampleMethodDescriptor extends ExampleMethodDescriptor {

    public OverloadedExampleMethodDescriptor(UniqueId uniqueId, ExampleMethodDescriptor toOverload, int id) {
        super(uniqueId, toOverload.getTargetMethod(), toOverload.gerContainerClass());
    }

	private static String displayName(ExampleMethodDescriptor exampleMethodDescriptor) {
		return String.format("Overloaded %s: %s", exampleMethodDescriptor.getLabel(), exampleMethodDescriptor.getTargetMethod().toString());
	}

}
