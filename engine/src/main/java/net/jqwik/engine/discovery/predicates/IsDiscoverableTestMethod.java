package net.jqwik.engine.discovery.predicates;

import java.lang.reflect.*;
import java.util.function.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

public class IsDiscoverableTestMethod implements Predicate<Method> {

	@Override
	public boolean test(Method candidate) {
		return (!isAbstract(candidate) && !isPrivate(candidate));
	}

}
