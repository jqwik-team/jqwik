package net.jqwik.engine.discovery.specs;

import java.lang.reflect.*;

import org.junit.platform.engine.support.hierarchical.Node.*;

import net.jqwik.engine.discovery.predicates.*;
import net.jqwik.engine.support.*;

import static org.junit.platform.commons.support.ModifierSupport.*;

import static net.jqwik.engine.descriptor.DiscoverySupport.*;

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
				"A @Property method must not have JUnit annotations: %s",
				JqwikStringSupport.displayString(findJUnitAnnotations(candidate))
			));
		return SkipResult.doNotSkip();
	}

	private boolean hasJupiterAnnotation(Method candidate) {
		return findJUnitAnnotations(candidate).size() > 0;
	}

}
