package net.jqwik.discovery;

import java.lang.reflect.Method;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

public class JqwikExampleTestDescriptor extends AbstractTestDescriptor {
    private final Method exampleMethod;
    private final Class containerClass;

    public JqwikExampleTestDescriptor(Method exampleMethod, Class containerClass, TestDescriptor parent) {
        super(parent.getUniqueId().append("method", exampleMethod.getName()), determineDisplayName(exampleMethod));
        this.exampleMethod = exampleMethod;
		this.containerClass = containerClass;
		setParent(parent);
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
    public boolean isContainer() {
        return false;
    }

    @Override
    public boolean isTest() {
        return true;
    }
}
