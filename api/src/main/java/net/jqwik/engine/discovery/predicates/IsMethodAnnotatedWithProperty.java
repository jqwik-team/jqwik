package net.jqwik.engine.discovery.predicates;

import java.lang.reflect.*;
import java.util.function.*;

import net.jqwik.api.*;

import static org.junit.platform.commons.support.AnnotationSupport.*;

public class IsMethodAnnotatedWithProperty implements Predicate<Method> {

	@Override
	public boolean test(Method method) {
		return isAnnotated(method, Property.class);
	}
}