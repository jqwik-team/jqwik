package net.jqwik.descriptor;

import java.lang.reflect.Method;

import org.junit.platform.engine.UniqueId;

import net.jqwik.api.ExampleContext;

public class ExampleMethodDescriptor extends AbstractMethodDescriptor implements ExampleContext {

    public ExampleMethodDescriptor(UniqueId uniqueId, Method exampleMethod, Class containerClass) {
        super(uniqueId, containerClass, exampleMethod);
    }

}
