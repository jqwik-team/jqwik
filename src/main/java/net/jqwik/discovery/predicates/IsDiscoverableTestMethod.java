package net.jqwik.discovery.predicates;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import static net.jqwik.support.JqwikReflectionSupport.isAbstract;
import static net.jqwik.support.JqwikReflectionSupport.isPrivate;

public class IsDiscoverableTestMethod implements Predicate<Method> {

	@Override
	public boolean test(Method candidate) {
		return (!isAbstract(candidate) && !isPrivate(candidate));
	}

}
