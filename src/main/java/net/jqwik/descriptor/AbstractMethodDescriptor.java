package net.jqwik.descriptor;

import java.lang.reflect.Method;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.execution.PropertyContext;

public abstract class AbstractMethodDescriptor extends AbstractTestDescriptor implements PropertyContext {
	private final Method targetMethod;
	private final Class containerClass;

	public AbstractMethodDescriptor(UniqueId uniqueId, Method targetMethod, Class containerClass) {
		super(uniqueId, determineDisplayName(targetMethod), MethodSource.from(targetMethod));
		this.containerClass = containerClass;
		this.targetMethod = targetMethod;
	}

	protected static String determineDisplayName(Method targetMethod) {
		return targetMethod.getName();
	}

	public Method getTargetMethod() {
		return targetMethod;
	}

	public Class<?> getContainerClass() {
		return containerClass;
	}

	public String getLabel() {
		return getDisplayName();
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}

}
