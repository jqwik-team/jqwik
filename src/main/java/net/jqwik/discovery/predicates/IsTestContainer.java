package net.jqwik.discovery.predicates;

import net.jqwik.support.JqwikReflectionSupport;
import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public class IsTestContainer implements Predicate<Class<?>> {

	private static final ExampleDiscoverySpec exampleSpec = new ExampleDiscoverySpec();
	private static final PropertyDiscoverySpec propertySpec = new PropertyDiscoverySpec();
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
		Predicate<Method> hasATestMethod = method -> exampleSpec.discover(method) || propertySpec.discover(method);
		return !ReflectionSupport.findMethods(candidate, hasATestMethod, HierarchyTraversalMode.TOP_DOWN).isEmpty();
	}

	private boolean hasGroups(Class<?> candidate) {
		return !JqwikReflectionSupport.findNestedClasses(candidate, isGroup).isEmpty();
	}

}
