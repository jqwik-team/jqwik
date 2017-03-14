package net.jqwik.descriptor;

import java.lang.reflect.Method;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

import net.jqwik.api.ExampleDescriptor;

public class ExampleMethodDescriptor extends AbstractTestDescriptor implements ExampleDescriptor {

	private final Method exampleMethod;
    private final Class containerClass;

    public ExampleMethodDescriptor(UniqueId uniqueId, Method exampleMethod, Class containerClass) {
        super(uniqueId, determineDisplayName(exampleMethod));
        this.exampleMethod = exampleMethod;
		this.containerClass = containerClass;
		setSource(new MethodSource(this.exampleMethod));
    }

	private static String determineDisplayName(Method exampleMethod) {
		return exampleMethod.getName();
	}

	public Method getExampleMethod() {
    	return exampleMethod;
	}

    public Class gerContainerClass() {
    	return containerClass;
	}

	@Override
	public String getLabel() {
		return getDisplayName();
	}

	@Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public boolean isTest() {
        return true;
    }
}
