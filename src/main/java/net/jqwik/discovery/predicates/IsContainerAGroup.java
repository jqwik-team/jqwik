package net.jqwik.discovery.predicates;

import static org.junit.platform.commons.support.AnnotationSupport.*;

import java.util.function.*;

import net.jqwik.api.*;

public class IsContainerAGroup implements Predicate<Class<?>> {

	private final static Predicate<Class<?>> isTopLevelClass = new IsTopLevelClass();

	@Override
	public boolean test(Class<?> candidate) {
		return isGroup(candidate) && !isTopLevelClass.test(candidate);
	}

	private boolean isGroup(Class<?> candidate) {
		return isAnnotated(candidate, Group.class);
	}

}
