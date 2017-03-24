package net.jqwik.descriptor;

import java.lang.reflect.*;

import org.junit.platform.engine.*;

import net.jqwik.api.properties.*;

public class PropertyMethodDescriptor extends AbstractMethodDescriptor implements PropertyContext {

	public PropertyMethodDescriptor(UniqueId uniqueId, Method exampleMethod, Class containerClass) {
        super(uniqueId, containerClass, exampleMethod);
    }
}
