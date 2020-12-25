package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure arbitraries that generate valid email addresses.
 *
 * <p>
 *    By default all variants of email addresses are generated.
 * </p>
 */
@API(status = EXPERIMENTAL, since = "1.3.9")
public interface EmailArbitrary extends Arbitrary<String> {

	/**
	 * Allow quoted local part.
	 *
	 * <p>
	 * Can be combined with other methods.
	 * </p>
	 *
	 * @return new instance of arbitrary
	 */
	EmailArbitrary quotedLocalPart();

	/**
	 * Allow unquoted local part.
	 *
	 * <p>
	 * Can be combined with other methods.
	 * </p>
	 *
	 * @return new instance of arbitrary
	 */
	EmailArbitrary unquotedLocalPart();

	/**
	 * Allow IPv4 addresses in the host part.
	 *
	 * <p>
	 * Can be combined with other methods.
	 * </p>
	 *
	 * @return new instance of arbitrary
	 */
	EmailArbitrary ipv4Host();

	/**
	 * Allow IPv6 addresses in the host part.
	 *
	 * <p>
	 * Can be combined with other methods.
	 * </p>
	 *
	 * @return new instance of arbitrary
	 */
	EmailArbitrary ipv6Host();

	/**
	 * Allow named web domains in the host part.
	 *
	 * <p>
	 * Can be combined with other methods.
	 * </p>
	 *
	 * @return new instance of arbitrary
	 */
	EmailArbitrary domainHost();

}
