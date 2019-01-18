package net.jqwik.engine.descriptor;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.commons.support.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;

import static net.jqwik.engine.descriptor.DiscoverySupport.*;

abstract class AbstractMethodDescriptor extends AbstractTestDescriptor implements JqwikDescriptor {
	private final Method targetMethod;
	private final Class containerClass;
	private final Set<TestTag> tags;
	private final Set<DomainContext> domainContexts;

	AbstractMethodDescriptor(UniqueId uniqueId, Method targetMethod, Class containerClass) {
		super(uniqueId, determineDisplayName(targetMethod), MethodSource.from(targetMethod));
		warnWhenJunitAnnotationsArePresent(targetMethod);
		this.tags = findTestTags(targetMethod);
		this.domainContexts = findDomainContexts(targetMethod);
		this.containerClass = containerClass;
		this.targetMethod = targetMethod;
	}

	private void warnWhenJunitAnnotationsArePresent(Method targetMethod) {
		DiscoverySupport.warnWhenJunitAnnotationsArePresent(targetMethod);
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
	public Set<DomainContext> getDomainContexts() {
		Set<DomainContext> allContexts = new LinkedHashSet<>(domainContexts);
		getParent().ifPresent(parentDescriptor -> {
			if (parentDescriptor instanceof JqwikDescriptor) {
				allContexts.addAll(((JqwikDescriptor) parentDescriptor).getDomainContexts());
			}
		});
		return allContexts;
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
