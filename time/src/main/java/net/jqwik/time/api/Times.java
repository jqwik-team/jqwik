package net.jqwik.time.api;

import org.apiguardian.api.*;

import net.jqwik.time.api.arbitraries.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.4.1")
public class Times {

	private Times() {
		// Must never be called
	}

	/**
	 * Create an arbitrary that generates instances of {@linkplain java.time.LocalTime}.
	 *
	 * @return a new arbitrary instance
	 */
	public static LocalTimeArbitrary times() {
		return null;
	}

}
