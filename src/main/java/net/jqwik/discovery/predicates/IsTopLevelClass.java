package net.jqwik.discovery.predicates;

import java.util.function.Predicate;

public class IsTopLevelClass implements Predicate<Class<?>> {

	@Override
	public boolean test(Class<?> candidate) {
		return candidate.getDeclaringClass() == null;
	}

}
