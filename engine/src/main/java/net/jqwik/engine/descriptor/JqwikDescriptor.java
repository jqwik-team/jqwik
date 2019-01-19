package net.jqwik.engine.descriptor;

import java.util.*;

import net.jqwik.api.domains.*;

public interface JqwikDescriptor {
	Set<Class<? extends DomainContext>> getDomainContexts();
}
