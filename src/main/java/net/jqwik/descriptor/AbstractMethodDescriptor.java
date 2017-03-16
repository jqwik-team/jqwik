package net.jqwik.descriptor;

import net.jqwik.api.TestContext;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;

import java.lang.reflect.Method;

public abstract class AbstractMethodDescriptor extends AbstractTestDescriptor implements TestContext {
	private final Method targetMethod;
	private final Class containerClass;

	public AbstractMethodDescriptor(UniqueId uniqueId, Class containerClass, Method targetMethod) {
		super(uniqueId, determineDisplayName(targetMethod));
		this.containerClass = containerClass;
		this.targetMethod = targetMethod;
		setSource(new MethodSource(this.targetMethod));
	}

	protected static String determineDisplayName(Method targetMethod) {
		return targetMethod.getName();
	}

	public Method getTargetMethod() {
    	return targetMethod;
	}

	public Class getContainerClass() {
    	return containerClass;
	}

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
