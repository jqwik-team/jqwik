package net.jqwik.web.api;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure arbitraries that generate valid email addresses.
 *
 * <p>
 *    By default only standard emails of the form {@code username@domain.tld} are generated.
 *    Other options like quoted local parts and ip addresses as host can be switched on.
 * </p>
 */
@API(status = MAINTAINED, since = "1.4.0")
public interface EmailArbitrary extends Arbitrary<String> {

	/**
	 * Allow the local part of an email to be quoted within {@literal "} characters.
	 *
	 * @return new instance of arbitrary
	 */
	EmailArbitrary allowQuotedLocalPart();


	/**
	 * Allow IPv4 addresses in the host part.
	 *
	 * @return new instance of arbitrary
	 */
	EmailArbitrary allowIpv4Host();

	/**
	 * Allow IPv6 addresses in the host part.
	 *
	 * @return new instance of arbitrary
	 */
	EmailArbitrary allowIpv6Host();

}
