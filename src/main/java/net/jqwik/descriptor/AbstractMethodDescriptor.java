package net.jqwik.descriptor;

import net.jqwik.api.*;
import net.jqwik.execution.*;
import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import java.lang.reflect.*;
import java.util.*;

public abstract class AbstractMethodDescriptor extends AbstractTestDescriptor implements PropertyContext {
	private final Method targetMethod;
	private final Class containerClass;

	public AbstractMethodDescriptor(UniqueId uniqueId, Method targetMethod, Class containerClass) {
		super(uniqueId, determineDisplayName(targetMethod), MethodSource.from(targetMethod));
		this.containerClass = containerClass;
		this.targetMethod = targetMethod;
	}

	protected static String determineDisplayName(Method targetMethod) {
		Optional<Label> label = AnnotationSupport.findAnnotation(targetMethod, Label.class);
		return label
			.map(Label::value)
			.filter(displayName -> !displayName.trim().isEmpty())
			.orElse(targetMethod.getName());
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
