package net.jqwik.engine.discovery.specs;

import java.util.function.*;

import org.junit.platform.engine.support.hierarchical.Node.*;

import net.jqwik.api.*;
import net.jqwik.engine.discovery.predicates.*;

import static org.junit.platform.commons.support.ModifierSupport.*;

public class GroupDiscoverySpec implements DiscoverySpec<Class<?>> {

	private final static Predicate<Class<?>> isPotentialTestContainer = new IsPotentialTestContainer();
	private final static Predicate<Class<?>> isTopLevelClass = new IsTopLevelClass();
	private final static Predicate<Class<?>> hasGroupAnnotation = candidate -> candidate.isAnnotationPresent(Group.class);

	@Override
	public boolean shouldBeDiscovered(Class<?> candidate) {
		return isPotentialTestContainer
			.and(isTopLevelClass.negate())
			.and(hasGroupAnnotation)
			.test(candidate);
	}
	@Override
	public SkipResult shouldBeSkipped(Class<?> candidate) {
		if (isStatic(candidate))
			return SkipResult.skip("@Group classes must not be static");
		return SkipResult.doNotSkip();
	}
}
