package net.jqwik.support;

import java.lang.reflect.*;
import java.util.*;

public class GenericsContext {
	private final Class<?> contextClass;
	private Set<Type> supertypes = new HashSet<>();

	GenericsContext(Class<?> contextClass) {
		this.contextClass = contextClass;
	}

	public Set<Type> genericSupertypes() {
		return Collections.unmodifiableSet(supertypes);
	}

	public Class<?> contextClass() {
		return contextClass;
	}

	void addGenericSupertype(Type supertype) {
		if (supertype == null)
			return;
		if (supertype == Object.class)
			return;
		supertypes.add(supertype);
	}

	@Override
	public String toString() {
		return String.format("GenericsContext(%s)", contextClass.getSimpleName());
	}
}
