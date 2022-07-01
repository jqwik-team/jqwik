package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.util.*;

// TODO: Make it immutable
public abstract class AllowedUnits<T> {

	protected Set<T> allowed;

	public AllowedUnits() {
		setDefaultAllowed();
	}

	protected abstract void setDefaultAllowed();

	@SafeVarargs
	public final void set(T... values) {
		allowed = new LinkedHashSet<>(Arrays.asList(values));
	}

	public Set<T> get() {
		return allowed;
	}

}
