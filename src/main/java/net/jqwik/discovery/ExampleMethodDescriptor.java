package net.jqwik.discovery;

import java.lang.reflect.Method;

import net.jqwik.api.ExampleDescriptor;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

public class ExampleMethodDescriptor extends AbstractTestDescriptor implements ExampleDescriptor {
	public static final String SEGMENT_TYPE = "example";
	public static final String SEGMENT_TYPE_OVERLOADED = "overloaded-example";

	private final Method exampleMethod;
    private final Class containerClass;

    public ExampleMethodDescriptor(ExampleMethodDescriptor toClone, int id, TestDescriptor parent) {
        super(parent.getUniqueId().append(SEGMENT_TYPE_OVERLOADED, toClone.getExampleMethod().getName() + "-" + id), determineDisplayName(toClone.getExampleMethod()));
        this.exampleMethod = toClone.getExampleMethod();
		this.containerClass = toClone.gerContainerClass();
		setParent(parent);
		setSource(new MethodSource(this.exampleMethod));
    }

    public ExampleMethodDescriptor(Method exampleMethod, Class containerClass, TestDescriptor parent) {
        super(parent.getUniqueId().append(SEGMENT_TYPE, exampleMethod.getName()), determineDisplayName(exampleMethod));
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
