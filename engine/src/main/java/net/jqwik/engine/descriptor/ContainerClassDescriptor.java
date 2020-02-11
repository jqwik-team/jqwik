package net.jqwik.engine.descriptor;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.api.domains.*;
import net.jqwik.engine.discovery.predicates.*;

import static net.jqwik.engine.descriptor.DiscoverySupport.*;

public class ContainerClassDescriptor extends AbstractTestDescriptor implements JqwikDescriptor {

	private final static Predicate<Class<?>> isTopLevelClass = new IsTopLevelClass();
	private final static Predicate<Class<?>> isContainerAGroup = new IsContainerAGroup();

	private final Class<?> containerClass;
	private final boolean isGroup;
	private final Set<TestTag> tags;
	private Set<Domain> domains;

	public ContainerClassDescriptor(UniqueId uniqueId, Class<?> containerClass, boolean isGroup) {
		super(uniqueId, determineDisplayName(containerClass), ClassSource.from(containerClass));
		warnWhenJunitAnnotationsArePresent(containerClass);
		this.tags = findTestTags(containerClass);
		this.domains = findDomains(containerClass);
		this.containerClass = containerClass;
		this.isGroup = isGroup;
	}

	private void warnWhenJunitAnnotationsArePresent(Class<?> containerClass) {
		DiscoverySupport.warnWhenJunitAnnotationsArePresent(containerClass);
	}

	private static String determineDisplayName(Class<?> containerClass) {
		return DiscoverySupport.determineLabel(containerClass, () -> getDefaultDisplayName(containerClass));
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
	public Set<TestTag> getTags() {
		return DiscoverySupport.getTags(getParent(), tags);
	}

	@Override
	public Set<Domain> getDomains() {
		return DiscoverySupport.getDomains(getJqwikParent(), domains);
	}

	@Override
	public AnnotatedElement getAnnotatedElement() {
		return getContainerClass();
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
