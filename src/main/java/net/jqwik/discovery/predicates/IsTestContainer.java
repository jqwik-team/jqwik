package net.jqwik.discovery.predicates;

import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public class IsTestContainer implements Predicate<Class<?>> {

	private static final IsExampleMethod isExampleMethod = new IsExampleMethod();

	private static final Predicate<Method> isAnyTestMethod = isExampleMethod;

	private static final IsPotentialTestContainer isPotentialTestContainer = new IsPotentialTestContainer();

	@Override
	public boolean test(Class<?> candidate) {
		if (!isPotentialTestContainer.test(candidate)) {
			return false;
		}
		return hasTests(candidate);
	}

	private boolean hasTests(Class<?> candidate) {
		return !ReflectionSupport.findMethods(candidate, isAnyTestMethod, HierarchyTraversalMode.TOP_DOWN).isEmpty();
	}

}
