package net.jqwik.discovery.predicates;

import static net.jqwik.support.JqwikReflectionSupport.*;

import java.lang.reflect.*;
import java.util.function.*;

public class IsDiscoverableTestMethod implements Predicate<Method> {

	@Override
	public boolean test(Method candidate) {
		return (!isAbstract(candidate) && !isPrivate(candidate));
	}

}
