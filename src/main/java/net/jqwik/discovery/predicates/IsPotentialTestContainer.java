package net.jqwik.discovery.predicates;

import static net.jqwik.support.JqwikReflectionSupport.*;

import java.util.function.Predicate;

public class IsPotentialTestContainer implements Predicate<Class<?>> {

	@Override
	public boolean test(Class<?> candidate) {
		if (isAbstract(candidate))
			return false;
		if (candidate.isLocalClass())
			return false;
		if (candidate.isAnonymousClass())
			return false;
		return (isStatic(candidate) || !candidate.isMemberClass());
	}

}
