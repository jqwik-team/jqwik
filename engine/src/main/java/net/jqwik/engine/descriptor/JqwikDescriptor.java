package net.jqwik.engine.descriptor;

import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.domains.*;

public interface JqwikDescriptor extends TestDescriptor {
	Set<Class<? extends DomainContext>> getDomainContexts();

	default Optional<JqwikDescriptor> getJqwikParent() {
		return getParent()
				   .filter(parent -> parent instanceof JqwikDescriptor)
				   .map(parent -> (JqwikDescriptor) parent);
	}
}
