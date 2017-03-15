package net.jqwik.descriptor;

import java.lang.reflect.Method;

import net.jqwik.api.PropertyDescriptor;
import org.junit.platform.engine.UniqueId;

public class PropertyMethodDescriptor extends AbstractMethodDescriptor implements PropertyDescriptor {

	public PropertyMethodDescriptor(UniqueId uniqueId, Method exampleMethod, Class containerClass) {
        super(uniqueId, containerClass, exampleMethod);
    }
}
