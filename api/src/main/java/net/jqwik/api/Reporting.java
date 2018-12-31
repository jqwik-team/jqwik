package net.jqwik.api;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.0")
public enum Reporting {
	GENERATED, FALSIFIED;

	public boolean containedIn(Reporting[] reporting) {
		return Arrays.stream(reporting).anyMatch(this::equals);
	}
}
