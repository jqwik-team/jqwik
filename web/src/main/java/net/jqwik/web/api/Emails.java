package net.jqwik.web.api;

import org.apiguardian.api.*;

import net.jqwik.web.*;

import static org.apiguardian.api.API.Status.*;

public class Emails {

	private Emails() {
	}

	/**
	 * Create an arbitrary that generates valid E-Mail addresses.
	 *
	 * @return a new arbitrary instance
	 */
	@API(status = EXPERIMENTAL, since = "1.4.0")
	public static EmailArbitrary emails() {
		return new DefaultEmailArbitrary();
	}


}
