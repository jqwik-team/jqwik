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
	 * Generates quoted local parts
	 */
	EmailArbitrary quotedLocalParts();

	/**
	 * Generates unquoted local parts
	 */
	EmailArbitrary unquotedLocalParts();

	/**
	 * Generates IPv4 addresses in the domain part
	 */
	EmailArbitrary ipv4Addresses();

	/**
	 * Generates IPv6 addresses in the domain part
	 */
	EmailArbitrary ipv6Addresses();

	/**
	 * Generates domains in the domain part
	 */
	EmailArbitrary domains();

}
