package net.jqwik.descriptor;

import org.junit.platform.engine.UniqueId;

public class OverloadedExampleMethodDescriptor extends ExampleMethodDescriptor {

    public OverloadedExampleMethodDescriptor(UniqueId uniqueId, ExampleMethodDescriptor toOverload, int id) {
        super(uniqueId, toOverload.getExampleMethod(), toOverload.gerContainerClass(), displayName(toOverload));
    }

	private static String displayName(ExampleMethodDescriptor exampleMethodDescriptor) {
		return String.format("Overloaded %s: %s", exampleMethodDescriptor.getLabel(), exampleMethodDescriptor.getExampleMethod().toString());
	}

}
