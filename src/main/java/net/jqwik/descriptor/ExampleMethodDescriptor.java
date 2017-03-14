package net.jqwik.descriptor;

import java.lang.reflect.Method;

import net.jqwik.api.ExampleDescriptor;
import net.jqwik.discovery.JqwikDiscoverer;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

public class ExampleMethodDescriptor extends AbstractTestDescriptor implements ExampleDescriptor {

	private final Method exampleMethod;
    private final Class containerClass;

    public ExampleMethodDescriptor(ExampleMethodDescriptor toOverload, int id) {
        this(toOverload.getUniqueId().append(JqwikDiscoverer.OVERLOADED_SEGMENT_TYPE, String.valueOf(id)), toOverload.getExampleMethod(), toOverload.gerContainerClass());
    }

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
