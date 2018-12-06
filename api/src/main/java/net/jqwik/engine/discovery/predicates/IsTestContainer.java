package net.jqwik.engine.discovery.predicates;

import java.lang.reflect.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;

public class IsTestContainer implements Predicate<Class<?>> {

	private static final Predicate<Method> isProperty = new IsProperty();

	private static final Predicate<Class<?>> isPotentialTestContainer = new IsPotentialTestContainer();
	private static final Predicate<Class<?>> isGroup = new IsContainerAGroup();

	@Override
	public boolean test(Class<?> candidate) {
		if (!isPotentialTestContainer.test(candidate)) {
			return false;
		}
		return hasTests(candidate) || hasGroups(candidate);
	}

	private boolean hasTests(Class<?> candidate) {
		return !ReflectionSupport.findMethods(candidate, isProperty, HierarchyTraversalMode.TOP_DOWN).isEmpty();
	}

	private boolean hasGroups(Class<?> candidate) {
		return !ReflectionSupport.findNestedClasses(candidate, isGroup).isEmpty();
	}

}
