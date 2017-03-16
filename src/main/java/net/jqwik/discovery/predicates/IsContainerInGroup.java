package net.jqwik.discovery.predicates;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

import java.util.function.Predicate;

import net.jqwik.api.Group;

public class IsContainerInGroup implements Predicate<Class<?>> {


	@Override
	public boolean test(Class<?> candidate) {
		if (!candidate.isMemberClass()) {
			return false;
		}
		return isGroup(candidate.getDeclaringClass());
	}

	private boolean isGroup(Class<?> candidate) {
		return isAnnotated(candidate, Group.class);
	}

}
