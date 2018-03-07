package net.jqwik.discovery.specs;

import net.jqwik.api.*;
import net.jqwik.discovery.predicates.*;
import net.jqwik.support.*;
import org.junit.platform.engine.support.hierarchical.Node.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

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
		if (hasJupiterAnnotation(candidate))
			return SkipResult.skip(String.format("A @Property method must not have Jupiter annotations: %s", JqwikStringSupport
				.displayString(findJupiterAnnotations(candidate))));
		return SkipResult.doNotSkip();
	}

	private boolean hasJupiterAnnotation(Method candidate) {
		return findJupiterAnnotations(candidate).size() > 0;
	}

	private List<Annotation> findJupiterAnnotations(Method candidate) {
		return Arrays.stream(candidate.getDeclaredAnnotations())
					 .filter(annotation -> annotation.annotationType()
													 .getPackage()
													 .getName()
													 .startsWith("org.junit.jupiter"))
					 .collect(Collectors.toList());
	}

}
