package net.jqwik.descriptor;

import java.lang.reflect.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.api.*;

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
	public Type getType() {
		return Type.TEST;
	}

}
