package net.jqwik.engine.discovery.specs;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import org.junit.platform.engine.support.hierarchical.Node.*;

import net.jqwik.engine.discovery.predicates.*;
import net.jqwik.engine.support.*;

import static org.junit.platform.commons.support.ModifierSupport.*;

public class PropertyDiscoverySpec implements DiscoverySpec<Method> {
	private final IsProperty isProperty = new IsProperty();

	@Override
	public boolean shouldBeDiscovered(Method candidate) {
		return isProperty.test(candidate);
	}

	@Override
	public SkipResult shouldBeSkipped(Method candidate) {
		if (isStatic(candidate))
			return SkipResult.skip("A @Property method must not be static");
		if (hasJupiterAnnotation(candidate))
			return SkipResult.skip(String.format(
				"A @Property method must not have Jupiter annotations: %s",
				JqwikStringSupport.displayString(findJupiterAnnotations(candidate))
			));
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
