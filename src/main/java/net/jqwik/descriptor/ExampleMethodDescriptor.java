package net.jqwik.descriptor;

import java.lang.reflect.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;

public class ExampleMethodDescriptor extends AbstractMethodDescriptor implements ExampleContext {

	public ExampleMethodDescriptor(UniqueId uniqueId, Method exampleMethod, Class containerClass) {
		super(uniqueId, containerClass, exampleMethod);
	}

}
