package net.jqwik.web;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.web.api.*;

public class DefaultEmailArbitrary extends ArbitraryDecorator<String> implements EmailArbitrary {

	private boolean allowQuotedLocalPart = false;
	private boolean allowUnquotedLocalPart = false;
	private boolean allowDomainHost = false;
	private boolean allowIPv4Host = false;
	private boolean allowIPv6Host = false;

	@Override
	protected Arbitrary<String> arbitrary() {
		Arbitrary<String> arbitraryLocalPart = localPart();
		Arbitrary<String> arbitraryDomain = host();
		return Combinators.combine(arbitraryLocalPart, arbitraryDomain)
						  .as((localPart, domain) -> localPart + "@" + domain);
	}

	private Arbitrary<String> localPart() {
		if (!allowUnquotedLocalPart && !allowQuotedLocalPart) {
			allowUnquotedLocalPart = true;
			allowQuotedLocalPart = true;
		}
		Arbitrary<String> unquoted = localPartUnquoted();
		Arbitrary<String> quoted = localPartQuoted();
		int frequencyUnquoted = allowUnquotedLocalPart ? 4 : 0;
		int frequencyQuoted = allowQuotedLocalPart ? 1 : 0;
		return Arbitraries.frequencyOf(
				Tuple.of(frequencyUnquoted, unquoted),
				Tuple.of(frequencyQuoted, quoted)
		);
	}

	private Arbitrary<String> localPartUnquoted() {
		Arbitrary<String> unquoted =
				Arbitraries.strings()
						   .alpha().numeric().withChars("!#$%&'*+-/=?^_`{|}~.")
						   .ofMinLength(1).ofMaxLength(64);
		unquoted = unquoted.filter(v -> !v.contains(".."));
		unquoted = unquoted.filter(v -> v.charAt(0) != '.');
		unquoted = unquoted.filter(v -> v.charAt(v.length() - 1) != '.');
		return unquoted.edgeCases(stringConfig -> stringConfig.includeOnly("A", "a", "0", "!"));
	}

	private Arbitrary<String> localPartQuoted() {
		Arbitrary<String> quoted =
				Arbitraries.strings()
						   .alpha().numeric().withChars(" !#$%&'*+-/=?^_`{|}~.\"(),:;<>@[\\]")
						   .ofMinLength(1).ofMaxLength(62);
		quoted = quoted.map(v -> "\"" + v.replace("\\", "\\\\")
										 .replace("\"", "\\\"") + "\"");
		quoted = quoted.filter(v -> v.length() <= 64);
		return quoted.edgeCases(stringConfig -> stringConfig.includeOnly("\"A\"", "\"a\"", "\" \""));
	}

	private Arbitrary<String> host() {
		if (!allowDomainHost && !allowIPv4Host && !allowIPv6Host) {
			allowDomainHost = true;
			allowIPv4Host = true;
			allowIPv6Host = true;
		}
		int frequencyDomain = allowDomainHost ? 4 : 0;
		int frequencyIPv4Addresses = allowIPv4Host ? 1 : 0;
		int frequencyIPv6Addresses = allowIPv6Host ? 1 : 0;
		return Arbitraries.frequencyOf(
				Tuple.of(frequencyDomain, webDomain()),
				Tuple.of(frequencyIPv4Addresses, hostIpv4()),
				Tuple.of(frequencyIPv6Addresses, hostIpv6())
		);
	}

	private Arbitrary<String> hostIpv4() {
		Arbitrary<Integer> addressPart =
				Arbitraries.integers().between(0, 255);
		return Combinators.combine(addressPart, addressPart, addressPart, addressPart)
						  .as((a, b, c, d) -> "[" + a + "." + b + "." + c + "." + d + "]")
						  .edgeCases(stringConfig -> stringConfig.includeOnly("[0.0.0.0]", "[255.255.255.255]").add("[127.0.0.1]"));
	}

	private Arbitrary<String> hostIpv6() {
		Arbitrary<List<String>> addressParts = ipv6Part().list().ofSize(8);
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
		if (notOnlyFirstColonClusterHasDoubleColon(ip)) {
			return false;
		}
		return true;
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
		Arbitrary<Integer> numberOfSubdomains =
				Arbitraries.integers()
						   .between(1, 25)
						   .edgeCases(integerConfig -> integerConfig.includeOnly(1, 25));
		Arbitrary<String> topLevelDomain = topLevelDomain();

		return numberOfSubdomains.flatMap(depth -> Arbitraries.recursive(
				() -> topLevelDomain,
				this::prependDomainPart,
				depth
		).filter(v -> v.length() <= 253));
	}

	private Arbitrary<String> prependDomainPart(Arbitrary<String> tail) {
		Arbitrary<String> domainPart = domainPart(1, 63);
		return Combinators.combine(domainPart, tail)
						  .as((part, rest) -> {
							  String newDomain = part + "." + rest;
							  // This is an optimization to have less filtering
							  if (newDomain.length() > 253) {
								  return rest;
							  }
							  return newDomain;
						  });
	}

	private Arbitrary<String> topLevelDomain() {
		return domainPart(2, 10).filter(this::notAllNumeric);
	}

	private boolean notAllNumeric(String tld) {
		for (char c : tld.toCharArray()) {
			if (c < '0' || c > '9') {
				return true;
			}
		}
		return false;
	}

	private Arbitrary<String> domainPart(int minLength, int maxLength) {
		//Not using .alpha().numeric().withChars("-") because runtime is too high
		//Using "." in withChars() to generate more subdomains
		return Arbitraries.strings()
						  .withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-")
						  .ofMinLength(minLength).ofMaxLength(maxLength)
						  .filter(this::validUseOfHyphensInDomainPart);
	}

	private boolean validUseOfHyphensInDomainPart(String domainPart) {
		boolean firstSignNotAHyphen = domainPart.charAt(0) != '-';
		boolean lastSignNotAHyphen = domainPart.charAt(domainPart.length() - 1) != '-';
		return firstSignNotAHyphen && lastSignNotAHyphen;
	}

	@Override
	public EmailArbitrary quotedLocalPart() {
		DefaultEmailArbitrary clone = typedClone();
		clone.allowQuotedLocalPart = true;
		return clone;
	}

	@Override
	public EmailArbitrary unquotedLocalPart() {
		DefaultEmailArbitrary clone = typedClone();
		clone.allowUnquotedLocalPart = true;
		return clone;
	}

	@Override
	public EmailArbitrary ipv4Host() {
		DefaultEmailArbitrary clone = typedClone();
		clone.allowIPv4Host = true;
		return clone;
	}

	@Override
	public EmailArbitrary ipv6Host() {
		DefaultEmailArbitrary clone = typedClone();
		clone.allowIPv6Host = true;
		return clone;
	}

	@Override
	public EmailArbitrary domainHost() {
		DefaultEmailArbitrary clone = typedClone();
		clone.allowDomainHost = true;
		return clone;
	}
}
