package net.jqwik.discovery.predicates;

import net.jqwik.api.Property;

import java.lang.reflect.Method;
import java.util.function.Predicate;

import static org.junit.platform.commons.support.AnnotationSupport.*;

public class IsMethodAnnotatedWithProperty implements Predicate<Method> {

	@Override
	public boolean test(Method method) {
		return isAnnotated(method, Property.class);
	}
}