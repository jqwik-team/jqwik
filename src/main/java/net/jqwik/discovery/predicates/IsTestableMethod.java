package net.jqwik.discovery.predicates;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;
import static net.jqwik.support.JqwikReflectionSupport.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Predicate;

abstract class IsTestableMethod implements Predicate<Method> {

	private final Class<? extends Annotation> annotationType;

	IsTestableMethod(Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public boolean test(Method candidate) {
		if (isStatic(candidate))
			return false;
		if (isPrivate(candidate))
			return false;
		if (isAbstract(candidate))
			return false;
		return isAnnotated(candidate, annotationType);
	}

}
