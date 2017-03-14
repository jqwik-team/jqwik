package net.jqwik.discovery.predicates;

import static net.jqwik.support.JqwikReflectionSupport.isPrivate;

import java.util.function.Predicate;

public class IsScannableContainerClass implements Predicate<Class<?>> {

	private static final IsTestContainer isTestContainer = new IsTestContainer();

	@Override
	public boolean test(Class<?> candidate) {
		if (isPrivate(candidate))
			return false;
		return isTestContainer.test(candidate);
	}

}
