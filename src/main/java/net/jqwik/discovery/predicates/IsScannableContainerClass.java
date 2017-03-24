package net.jqwik.discovery.predicates;

import static net.jqwik.support.JqwikReflectionSupport.*;

import java.util.function.*;

public class IsScannableContainerClass implements Predicate<Class<?>> {

	private static final IsTestContainer isTestContainer = new IsTestContainer();

	@Override
	public boolean test(Class<?> candidate) {
		if (isPrivate(candidate))
			return false;
		return isTestContainer.test(candidate);
	}

}
