package net.jqwik.web.api;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.web.*;

import static org.apiguardian.api.API.Status.*;

/**
 * This is the home for static methods to produce arbitraries for Web-related
 * domain types, like email addresses, ip addresses, domains, URLs etc.
 */
@API(status = EXPERIMENTAL, since = "1.4.0")
public class Web {

	private Web() {
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

	/**
	 * Create an arbitrary that generates valid internet domain names.
	 *
	 * @return a new arbitrary instance
	 */
	@API(status = EXPERIMENTAL, since = "1.5.5")
	public static Arbitrary<String> webDomains() {
		return new DefaultWebDomainArbitrary();
	}

}
