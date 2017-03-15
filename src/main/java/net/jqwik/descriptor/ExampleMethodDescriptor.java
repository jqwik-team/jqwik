package net.jqwik.descriptor;

import java.lang.reflect.Method;

import org.junit.platform.engine.UniqueId;

import net.jqwik.api.ExampleDescriptor;

public class ExampleMethodDescriptor extends AbstractMethodDescriptor implements ExampleDescriptor {

    public ExampleMethodDescriptor(UniqueId uniqueId, Method exampleMethod, Class containerClass) {
        super(uniqueId, containerClass, exampleMethod);
    }

}
