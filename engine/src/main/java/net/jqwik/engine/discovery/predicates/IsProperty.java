package net.jqwik.engine.discovery.predicates;

import java.lang.reflect.*;
import java.util.function.*;

public class IsProperty implements Predicate<Method> {
	private final static IsDiscoverableTestMethod isDiscoverableTestMethod = new IsDiscoverableTestMethod();
	private final static IsMethodAnnotatedWithProperty isMethodAnnotatedWithProperty = new IsMethodAnnotatedWithProperty();

	@Override
	public boolean test(Method method) {
		return isDiscoverableTestMethod.and(isMethodAnnotatedWithProperty).test(method);
	}

}