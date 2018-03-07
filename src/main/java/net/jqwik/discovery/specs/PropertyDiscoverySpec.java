package net.jqwik.discovery.specs;

import net.jqwik.api.*;
import net.jqwik.discovery.predicates.*;
import org.junit.platform.engine.support.hierarchical.Node.*;

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
	public SkipResult shouldBeSkipped(Method candidate) {
		if (isStatic(candidate))
			return SkipResult.skip("A @Property method must not be static");
		return SkipResult.doNotSkip();
	}

}
