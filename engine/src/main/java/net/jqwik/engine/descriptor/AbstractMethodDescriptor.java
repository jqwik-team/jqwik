package net.jqwik.engine.descriptor;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.api.*;

abstract class AbstractMethodDescriptor extends AbstractTestDescriptor {
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

	public Reporting[] getReporting() {
		Optional<Report> optionalReport = AnnotationSupport.findAnnotation(getTargetMethod(), Report.class);
		return optionalReport.map(Report::value).orElse(new Reporting[0]);
	}

}
