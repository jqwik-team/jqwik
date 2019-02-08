package net.jqwik.engine.discovery.specs;

import java.util.function.*;

import org.junit.platform.engine.support.hierarchical.Node.*;

import net.jqwik.api.*;
import net.jqwik.engine.discovery.predicates.*;

import static org.junit.platform.commons.support.ModifierSupport.*;

public class TopLevelContainerDiscoverySpec implements DiscoverySpec<Class<?>> {

	private final static Predicate<Class<?>> isPotentialTestContainer = new IsPotentialTestContainer();
	private final static Predicate<Class<?>> isTopLevelClass = new IsTopLevelClass();
	private final static Predicate<Class<?>> isGroup = candidate -> candidate.isAnnotationPresent(Group.class);
	private final static Predicate<Class<?>> isStaticNonGroupMember = candidate -> isStatic(candidate) && !isGroup.test(candidate);

	@Override
	public boolean shouldBeDiscovered(Class<?> candidate) {
		return isPotentialTestContainer
			.and(isTopLevelClass.or(isStaticNonGroupMember))
			.test(candidate);
	}

	@Override
	public SkipResult shouldBeSkipped(Class<?> candidate) {
		return SkipResult.doNotSkip();
	}
}
