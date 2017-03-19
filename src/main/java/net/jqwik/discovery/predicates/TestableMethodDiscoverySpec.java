package net.jqwik.discovery.predicates;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static net.jqwik.support.JqwikReflectionSupport.isStatic;
import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

abstract public class TestableMethodDiscoverySpec implements DiscoverySpec<Method> {

	private final static IsDiscoverableTestMethod isDiscoverableTestMethod = new IsDiscoverableTestMethod();

	private final Class<? extends Annotation> annotationType;

	TestableMethodDiscoverySpec(Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public boolean discover(Method candidate) {
		return isDiscoverableTestMethod.test(candidate) && isAnnotated(candidate, annotationType);
	}

	@Override
	public boolean butSkip(Method candidate) {
		return isStatic(candidate);
	}

}
