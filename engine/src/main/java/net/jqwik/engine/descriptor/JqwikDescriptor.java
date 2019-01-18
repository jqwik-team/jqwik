package net.jqwik.engine.descriptor;

import java.util.*;

import net.jqwik.api.domains.*;

public interface JqwikDescriptor {
	Set<DomainContext> getDomainContexts();
}
