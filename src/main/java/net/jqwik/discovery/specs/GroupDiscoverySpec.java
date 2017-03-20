package net.jqwik.discovery.specs;

import net.jqwik.api.Group;
import net.jqwik.discovery.predicates.IsPotentialTestContainer;
import net.jqwik.discovery.predicates.IsTopLevelClass;

import java.util.function.Predicate;

import static net.jqwik.support.JqwikReflectionSupport.isStatic;

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
	public boolean butSkippedOnExecution(Class<?> candidate) {
		return isStatic(candidate);
	}

	@Override
	public String skippingReason(Class<?> candidate) {
		return "@Group classes must not be static";
	}
}
