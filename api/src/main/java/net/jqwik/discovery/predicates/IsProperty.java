package net.jqwik.discovery.predicates;

import java.lang.reflect.Method;
import java.util.function.Predicate;

public class IsProperty implements Predicate<Method> {
	private final static IsDiscoverableTestMethod isDiscoverableTestMethod = new IsDiscoverableTestMethod();
	private final static IsMethodAnnotatedWithProperty isMethodAnnotatedWithProperty = new IsMethodAnnotatedWithProperty();

	@Override
	public boolean test(Method method) {
		return isDiscoverableTestMethod.and(isMethodAnnotatedWithProperty).test(method);
	}

}