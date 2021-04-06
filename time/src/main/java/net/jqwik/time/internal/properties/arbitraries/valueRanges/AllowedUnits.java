package net.jqwik.time.internal.properties.arbitraries.valueRanges;

import java.util.*;

public abstract class AllowedUnits<T> {

	protected Set<T> allowed;

	public AllowedUnits() {
		setDefaultAllowed();
	}

	protected abstract void setDefaultAllowed();

	public void set(T... values) {
		allowed = new HashSet<>(Arrays.asList(values));
	}

	public Set<T> get() {
		return allowed;
	}

}
