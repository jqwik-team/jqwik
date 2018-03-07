package net.jqwik.descriptor;

import net.jqwik.api.*;
import net.jqwik.discovery.predicates.*;
import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import java.util.*;
import java.util.function.*;

public class ContainerClassDescriptor extends AbstractTestDescriptor {

	private final static Predicate<Class<?>> isTopLevelClass = new IsTopLevelClass();
	private final static Predicate<Class<?>> isContainerAGroup = new IsContainerAGroup();

	private final Class<?> containerClass;
	private final boolean isGroup;

	public ContainerClassDescriptor(UniqueId uniqueId, Class<?> containerClass, boolean isGroup) {
		super(uniqueId, determineDisplayName(containerClass), ClassSource.from(containerClass));
		this.containerClass = containerClass;
		this.isGroup = isGroup;
	}

	private static String determineDisplayName(Class<?> containerClass) {
		Optional<Label> label = AnnotationSupport.findAnnotation(containerClass, Label.class);
		return label
			.map(Label::value)
			.filter(displayName -> !displayName.trim().isEmpty())
			.orElse(getDefaultDisplayName(containerClass));
	}

	private static String getDefaultDisplayName(Class<?> containerClass) {
		if (isTopLevelClass.test(containerClass) || isContainerAGroup.test(containerClass))
			return containerClass.getSimpleName();
		return getCanonicalNameWithoutPackage(containerClass);
	}

	private static String getCanonicalNameWithoutPackage(Class<?> containerClass) {
		String packageName = containerClass.getPackage().getName();
		String canonicalName = containerClass.getCanonicalName();
		return canonicalName.substring(packageName.length() + 1);
	}

	@Override
	public Type getType() {
		return Type.CONTAINER;
	}

	public Class<?> getContainerClass() {
		return containerClass;
	}

	public boolean isGroup() {
		return isGroup;
	}
}
