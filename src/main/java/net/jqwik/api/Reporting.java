package net.jqwik.api;

import java.util.*;

public enum Reporting {
	GENERATED,
	/**
	 * @deprecated Currently switched off because it did not actually do what it was supposed to do
	 */
	@Deprecated FALSIFIED;

	public boolean containedIn(Reporting[] reporting) {
		return Arrays.stream(reporting).anyMatch(this::equals);
	}
}
