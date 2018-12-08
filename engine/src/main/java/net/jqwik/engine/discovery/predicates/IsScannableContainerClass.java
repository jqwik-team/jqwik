package net.jqwik.engine.discovery.predicates;

import java.util.function.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

public class IsScannableContainerClass implements Predicate<Class<?>> {

	private static final IsTestContainer isTestContainer = new IsTestContainer();

	@Override
	public boolean test(Class<?> candidate) {
		if (isPrivate(candidate))
			return false;
		return isTestContainer.test(candidate);
	}

}
