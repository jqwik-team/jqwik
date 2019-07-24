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
	private final Set<Domain> domains;

	AbstractMethodDescriptor(UniqueId uniqueId, Method targetMethod, Class containerClass) {
		super(uniqueId, determineDisplayName(targetMethod), MethodSource.from(targetMethod));
		warnWhenJunitAnnotationsArePresent(targetMethod);
		this.tags = findTestTags(targetMethod);
		this.domains = findDomains(targetMethod);
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
	public Set<TestTag> getTags() {
		return DiscoverySupport.getTags(getParent(), tags);
	}

	@Override
	public Set<Domain> getDomains() {
		return DiscoverySupport.getDomains(getJqwikParent(), domains);
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
