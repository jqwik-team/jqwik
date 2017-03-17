package net.jqwik.discovery.predicates;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;

import net.jqwik.support.JqwikReflectionSupport;

public class IsTestContainer implements Predicate<Class<?>> {

	private static final IsExampleMethod isExampleMethod = new IsExampleMethod();
	private static final IsPropertyMethod isPropertyMethod = new IsPropertyMethod();
	private static final Predicate<Method> isAnyTestMethod = isExampleMethod.or(isPropertyMethod);
	private static final IsContainerAGroup isGroup = new IsContainerAGroup();

	private static final IsPotentialTestContainer isPotentialTestContainer = new IsPotentialTestContainer();

	@Override
	public boolean test(Class<?> candidate) {
		if (!isPotentialTestContainer.test(candidate)) {
			return false;
		}
		return hasTests(candidate) || hasGroups(candidate);
	}

	private boolean hasTests(Class<?> candidate) {
		return !ReflectionSupport.findMethods(candidate, isAnyTestMethod, HierarchyTraversalMode.TOP_DOWN).isEmpty();
	}

	private boolean hasGroups(Class<?> candidate) {
		return !JqwikReflectionSupport.findNestedClasses(candidate, isGroup).isEmpty();
	}

}
