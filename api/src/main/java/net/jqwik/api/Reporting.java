package net.jqwik.api;

import java.util.*;

public enum Reporting {
	GENERATED, FALSIFIED;

	public boolean containedIn(Reporting[] reporting) {
		return Arrays.stream(reporting).anyMatch(this::equals);
	}
}
