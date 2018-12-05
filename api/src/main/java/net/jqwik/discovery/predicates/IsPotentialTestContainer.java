package net.jqwik.discovery.predicates;

import java.util.function.*;

import static net.jqwik.support.JqwikReflectionSupport.*;

public class IsPotentialTestContainer implements Predicate<Class<?>> {

	@Override
	public boolean test(Class<?> candidate) {
		if (isAbstract(candidate))
			return false;
		if (isPrivate(candidate))
			return false;
		if (candidate.isLocalClass())
			return false;
		return !candidate.isAnonymousClass();
	}

}
