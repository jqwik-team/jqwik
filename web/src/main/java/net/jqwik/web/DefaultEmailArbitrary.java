package net.jqwik.web;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.web.api.*;

public class DefaultEmailArbitrary extends ArbitraryDecorator<String> implements EmailArbitrary {

	private boolean allowQuotedLocalPart = false;
	private boolean allowIPv4Host = false;
	private boolean allowIPv6Host = false;

	@Override
	protected Arbitrary<String> arbitrary() {
		Arbitrary<String> arbitraryLocalPart = localPart();
		Arbitrary<String> arbitraryHost = host();
		return Combinators.combine(arbitraryLocalPart, arbitraryHost)
						  .as((localPart, domain) -> localPart + "@" + domain);
	}

	private Arbitrary<String> localPart() {
		Arbitrary<String> unquoted = localPartUnquoted();
		Arbitrary<String> quoted = localPartQuoted();
		int frequencyUnquoted = 4;
		int frequencyQuoted = allowQuotedLocalPart ? 1 : 0;
		return Arbitraries.frequencyOf(
			Tuple.of(frequencyUnquoted, unquoted),
			Tuple.of(frequencyQuoted, quoted)
		);
	}

	private Arbitrary<String> localPartUnquoted() {
		Arbitrary<String> unquoted =
			Arbitraries.strings()
					   .withChars("abcdefghijklmnopqrstuvwxyz")
					   .withChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
					   .withChars("0123456789!#$%&'*+-/=?^_`{|}~.")
					   //.alpha().numeric().withChars("!#$%&'*+-/=?^_`{|}~.")
					   .ofMinLength(1).ofMaxLength(64);

		// No double dot, no dot at beginning or end of local part allowed.
		// Single filter shrinks faster than three smaller ones.
		unquoted = unquoted.filter(
			v -> !v.contains("..")
					 && v.charAt(0) != '.'
					 && v.charAt(v.length() - 1) != '.'
		);

		return unquoted.edgeCases(stringConfig -> stringConfig.includeOnly("A", "a", "0"));
	}

	private Arbitrary<String> localPartQuoted() {
		Arbitrary<String> quoted =
			Arbitraries.strings()
					   .withChars("abcdefghijklmnopqrstuvwxyz")
					   .withChars("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
					   .withChars("0123456789 !#$%&'*+-/=?^_`{|}~.\"(),:;<>@[\\]")
					   // .alpha().numeric().withChars(" !#$%&'*+-/=?^_`{|}~.\"(),:;<>@[\\]")
					   .ofMinLength(1).ofMaxLength(62);
		quoted = quoted.map(v -> "\"" + v.replace("\\", "\\\\")
										 .replace("\"", "\\\"") + "\"");
		quoted = quoted.filter(v -> v.length() <= 64);
		return quoted.edgeCases(stringConfig -> stringConfig
			.includeOnly("\"a\"")
			.add("\" \""));
	}

	private Arbitrary<String> host() {
		int frequencyDomain = 4;
		int frequencyIPv4Addresses = allowIPv4Host ? 1 : 0;
		int frequencyIPv6Addresses = allowIPv6Host ? 1 : 0;
		return Arbitraries.frequencyOf(
			Tuple.of(frequencyDomain, webDomain()),
			Tuple.of(frequencyIPv4Addresses, hostIpv4()),
			Tuple.of(frequencyIPv6Addresses, hostIpv6())
		);
	}

	private Arbitrary<String> hostIpv4() {
		Arbitrary<Integer> addressPart = Arbitraries.integers().between(0, 255).edgeCases(c -> c.includeOnly(0, 255));
		return Combinators.combine(addressPart, addressPart, addressPart, addressPart)
						  .as((a, b, c, d) -> "[" + a + "." + b + "." + c + "." + d + "]")
						  .edgeCases(stringConfig -> stringConfig.includeOnly("[0.0.0.0]", "[255.255.255.255]").add("[127.0.0.1]"));
	}

	private Arbitrary<String> hostIpv6() {
		Arbitrary<List<? extends String>> addressParts = ipv6Part().list().ofSize(8);
		Arbitrary<String> plainAddress = addressParts.map(parts -> String.join(":", parts));
		return plainAddress
			.map(this::removeThreeOrMoreColons)
			.filter(this::validUseOfColonInIPv6Address)
			.map(plain -> "[" + plain + "]")
			.edgeCases(stringConfig -> stringConfig.includeOnly(
				"[::]",
				"[0:0:0:0:0:0:0:0]",
				"[ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff]",
				"[FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF]"
			));
	}

	private boolean validUseOfColonInIPv6Address(String ip) {
		if (hasSingleColonAtStartOrEnd(ip)) {
			return false;
		}
		return !notOnlyFirstColonClusterHasDoubleColon(ip);
	}

	private static boolean notOnlyFirstColonClusterHasDoubleColon(String ip) {
		boolean first = true;
		boolean inCheck = false;
		for (int i = 0; i < ip.length() - 1; i++) {
			boolean ipContainsTwoColonsAtI = ip.charAt(i) == ':' && (ip.charAt(i + 1) == ':');
			if (first) {
				if (ipContainsTwoColonsAtI) {
					first = false;
					inCheck = true;
				}
			} else if (ipContainsTwoColonsAtI && !inCheck) {
				return true;
			} else if (!ipContainsTwoColonsAtI) {
				inCheck = false;
			}
		}
		return false;
	}

	private static boolean hasSingleColonAtStartOrEnd(String ip) {
		boolean startsWithOnlyOneColon = ip.charAt(0) == ':' && ip.charAt(1) != ':';
		boolean endsWithOnlyOneColon = ip.charAt(ip.length() - 1) == ':' && ip.charAt(ip.length() - 2) != ':';
		return startsWithOnlyOneColon || endsWithOnlyOneColon;
	}

	private Arbitrary<String> ipv6Part() {
		Arbitrary<Integer> ipv6PartNumber = Arbitraries.integers().between(0, 0xffff);
		return Arbitraries.frequencyOf(
			Tuple.of(1, Arbitraries.just("")),
			Tuple.of(8, ipv6PartNumber.map(this::toLowerHex)),
			Tuple.of(1, ipv6PartNumber.map(this::toUpperHex))
		);
	}

	private String toLowerHex(int ipv6Part) {
		return Integer.toHexString(ipv6Part);
	}

	private String toUpperHex(int ipv6Part) {
		return toLowerHex(ipv6Part).toUpperCase();
	}

	private String removeThreeOrMoreColons(String address) {
		while (address.contains(":::")) {
			address = address.replace(":::", "::");
		}
		return address;
	}

	private Arbitrary<String> webDomain() {
		return Web.webDomains();
	}

	@Override
	public EmailArbitrary allowQuotedLocalPart() {
		DefaultEmailArbitrary clone = typedClone();
		clone.allowQuotedLocalPart = true;
		return clone;
	}

	@Override
	public EmailArbitrary allowIpv4Host() {
		DefaultEmailArbitrary clone = typedClone();
		clone.allowIPv4Host = true;
		return clone;
	}

	@Override
	public EmailArbitrary allowIpv6Host() {
		DefaultEmailArbitrary clone = typedClone();
		clone.allowIPv6Host = true;
		return clone;
	}
}
