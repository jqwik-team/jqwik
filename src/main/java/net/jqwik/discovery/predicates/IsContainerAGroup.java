package net.jqwik.discovery.predicates;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

import java.util.function.Predicate;

import net.jqwik.api.Group;

public class IsContainerAGroup implements Predicate<Class<?>> {


	@Override
	public boolean test(Class<?> candidate) {
		if (!candidate.isMemberClass()) {
			return false;
		}
		return isGroup(candidate) && candidate.getDeclaringClass() != null;
	}

	private boolean isGroup(Class<?> candidate) {
		return isAnnotated(candidate, Group.class);
	}

}
