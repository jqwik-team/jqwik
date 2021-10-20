package net.jqwik.engine.discovery.predicates;

import java.util.function.*;

import static org.junit.platform.commons.support.ModifierSupport.*;

public class IsPotentialTestContainer implements Predicate<Class<?>> {

	@Override
	public boolean test(Class<?> candidate) {
		if (isAbstract(candidate))
			return false;
		if (isPrivate(candidate))
			return false;
		if (candidate.isEnum())
			return false;
		if (candidate.isLocalClass())
			return false;
		return !candidate.isAnonymousClass();
	}

}
