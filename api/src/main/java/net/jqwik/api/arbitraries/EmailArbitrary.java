package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure arbitraries that generate email values.
 */
@API(status = EXPERIMENTAL, since = "1.3.9")
public interface EmailArbitrary extends Arbitrary<String> {

	/**
	 * Allow quoted local parts
	 */
	EmailArbitrary quotedLocalParts();

	/**
	 * Allow unquoted local parts
	 */
	EmailArbitrary unquotedLocalParts();

	/**
	 * Allow IPv4 addresses in the domain
	 */
	EmailArbitrary ipv4Addresses();

	/**
	 * Allow IPv6 addresses in the domain
	 */
	EmailArbitrary ipv6Addresses();

	/**
	 * Allow domains in the domain
	 */
	EmailArbitrary domains();

}
