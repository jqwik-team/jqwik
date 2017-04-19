package net.jqwik.discovery.specs;

import net.jqwik.api.properties.*;
import net.jqwik.discovery.predicates.*;

import java.lang.reflect.*;

import static net.jqwik.support.JqwikReflectionSupport.*;
import static org.junit.platform.commons.support.AnnotationSupport.*;

public class PropertyDiscoverySpec implements DiscoverySpec<Method> {
	private final static IsDiscoverableTestMethod isDiscoverableTestMethod = new IsDiscoverableTestMethod();

	@Override
	public boolean shouldBeDiscovered(Method candidate) {
		return isDiscoverableTestMethod.test(candidate) && isAnnotated(candidate, Property.class);
	}

	@Override
	public boolean butSkippedOnExecution(Method candidate) {
		return isStatic(candidate);
	}

	@Override
	public String skippingReason(Method candidate) {
		return "A @Property method must not be static";
	}
}
