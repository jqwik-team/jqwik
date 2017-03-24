package net.jqwik.discovery.specs;

import static net.jqwik.support.JqwikReflectionSupport.*;
import static org.junit.platform.commons.support.AnnotationSupport.*;

import java.lang.annotation.*;
import java.lang.reflect.*;

import net.jqwik.discovery.predicates.*;

abstract public class TestableMethodDiscoverySpec implements DiscoverySpec<Method> {

	private final static IsDiscoverableTestMethod isDiscoverableTestMethod = new IsDiscoverableTestMethod();

	private final Class<? extends Annotation> annotationType;

	TestableMethodDiscoverySpec(Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public boolean shouldBeDiscovered(Method candidate) {
		return isDiscoverableTestMethod.test(candidate) && isAnnotated(candidate, annotationType);
	}

	@Override
	public boolean butSkippedOnExecution(Method candidate) {
		return isStatic(candidate);
	}

}
