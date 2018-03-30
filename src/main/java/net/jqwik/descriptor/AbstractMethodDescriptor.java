package net.jqwik.descriptor;

import net.jqwik.execution.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import java.lang.reflect.*;
import java.util.*;

abstract class AbstractMethodDescriptor extends AbstractTestDescriptor implements PropertyContext {
	private final Method targetMethod;
	private final Class containerClass;
	private final Set<TestTag> tags;

	AbstractMethodDescriptor(UniqueId uniqueId, Method targetMethod, Class containerClass) {
		super(uniqueId, determineDisplayName(targetMethod), MethodSource.from(targetMethod));
		this.tags = determineTags(targetMethod);
		this.containerClass = containerClass;
		this.targetMethod = targetMethod;
	}

	private Set<TestTag> determineTags(Method targetMethod) {
		return DiscoverySupport.findTestTags(targetMethod);
	}

	private static String determineDisplayName(Method targetMethod) {
		return DiscoverySupport.determineLabel(targetMethod, targetMethod::getName);
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
	public Set<TestTag> getTags() {
		Set<TestTag> allTags = new LinkedHashSet<>(tags);
		getParent().ifPresent(parentDescriptor -> allTags.addAll(parentDescriptor.getTags()));
		return allTags;
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}

}
