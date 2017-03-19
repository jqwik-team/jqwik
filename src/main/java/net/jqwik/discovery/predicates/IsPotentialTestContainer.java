package net.jqwik.discovery.predicates;

import java.util.function.Predicate;

import static net.jqwik.support.JqwikReflectionSupport.isAbstract;
import static net.jqwik.support.JqwikReflectionSupport.isPrivate;

public class IsPotentialTestContainer implements Predicate<Class<?>> {

	@Override
	public boolean test(Class<?> candidate) {
		if (isAbstract(candidate))
			return false;
		if (isPrivate(candidate))
			return false;
		if (candidate.isLocalClass())
			return false;
		if (candidate.isAnonymousClass())
			return false;
		return true;
	}

}
