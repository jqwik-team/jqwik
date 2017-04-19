package net.jqwik.discovery.predicates;

import java.lang.reflect.*;
import java.util.function.*;

import static net.jqwik.support.JqwikReflectionSupport.*;

public class IsDiscoverableTestMethod implements Predicate<Method> {

	@Override
	public boolean test(Method candidate) {
		return (!isAbstract(candidate) && !isPrivate(candidate));
	}

}
