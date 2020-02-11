package net.jqwik.engine.descriptor;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.*;

import net.jqwik.api.domains.*;

public interface JqwikDescriptor extends TestDescriptor {
	Set<Domain> getDomains();

	AnnotatedElement getAnnotatedElement();

	default Optional<JqwikDescriptor> getJqwikParent() {
		return getParent()
				   .filter(parent -> parent instanceof JqwikDescriptor)
				   .map(parent -> (JqwikDescriptor) parent);
	}
}
