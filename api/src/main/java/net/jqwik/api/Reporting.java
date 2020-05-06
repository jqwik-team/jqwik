package net.jqwik.api;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.0")
public enum Reporting {
	/**
	 * {@link Reporting#GENERATED} will report each generated set of the parameters.
	 * This means that after each property test, summary table will be printed.
	 */
	GENERATED,

	/**
	 * {@link Reporting#FALSIFIED} will report each set of parameters that is falsified during shrinking.
	 * i.e., report "table" will be printed only when some test fails.
	 */
	FALSIFIED;

	public boolean containedIn(Reporting[] reporting) {
		return Arrays.stream(reporting).anyMatch(this::equals);
	}
}
