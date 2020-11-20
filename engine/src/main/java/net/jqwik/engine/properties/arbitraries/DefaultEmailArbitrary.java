package net.jqwik.engine.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultEmailArbitrary extends ArbitraryDecorator<String> implements EmailArbitrary {

	private boolean allowQuotedLocalPart = false;
	private boolean allowUnquotedLocalPart = false;
	private boolean allowDomainHost = false;
	private boolean allowIPv4Host = false;
	private boolean allowIPv6Host = false;

	@Override
	protected Arbitrary<String> arbitrary() {
		Arbitrary<String> arbitraryLocalPart = localPart();
		Arbitrary<String> arbitraryDomain = webDomain();
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
		return unquoted;
	}

	private Arbitrary<String> localPartQuoted() {
		Arbitrary<String> quoted =
				Arbitraries.strings()
						   .alpha().numeric().withChars(" !#$%&'*+-/=?^_`{|}~.\"(),:;<>@[\\]")
						   .ofMinLength(1).ofMaxLength(62);
		quoted = quoted.map(v -> "\"" + v.replace("\\", "\\\\")
										 .replace("\"", "\\\"") + "\"");
		quoted = quoted.filter(v -> v.length() <= 64);
		return quoted;
	}

	private Arbitrary<String> webDomain() {
		if (!allowDomainHost && !allowIPv4Host && !allowIPv6Host) {
			allowDomainHost = true;
			allowIPv4Host = true;
			allowIPv6Host = true;
		}
		int frequencyDomain = allowDomainHost ? 4 : 0;
		int frequencyIPv4Addresses = allowIPv4Host ? 1 : 0;
		int frequencyIPv6Addresses = allowIPv6Host ? 1 : 0;
		return Arbitraries.frequencyOf(
				Tuple.of(frequencyDomain, domainDomain()),
				Tuple.of(frequencyIPv4Addresses, domainIPv4()),
				Tuple.of(frequencyIPv6Addresses, domainIPv6())
		);
	}

	private Arbitrary<String> domainIPv4() {
		Arbitrary<Integer> addressPart = Arbitraries.integers().between(0, 255);
		return Combinators.combine(addressPart, addressPart, addressPart, addressPart)
						  .as((a, b, c, d) -> "[" + a + "." + b + "." + c + "." + d + "]");
	}

	private Arbitrary<String> domainIPv6() {
		Arbitrary<String> addressPart =
				Arbitraries.strings()
						   .numeric().withChars('a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F')
						   .ofMaxLength(4);
		Arbitrary<String> address =
				Combinators.combine(addressPart, addressPart, addressPart, addressPart, addressPart, addressPart, addressPart, addressPart)
						   .as((a, b, c, d, e, f, g, h) -> "[" + a + ":" + b + ":" + c + ":" + d + ":" + e + ":" + f + ":" + g + ":" + h + "]");
		address = address.filter(v -> validUseOfColonInIPv6Address(v.substring(1, v.length() - 1)));
		return address;
	}

	public static boolean validUseOfColonInIPv6Address(String ip) {
		if (!checkColonPlacement(ip)) {
			return false;
		}
		boolean first = true;
		boolean inCheck = false;
		for (int i = 0; i < ip.length() - 1; i++) {
			boolean ipContainsTwoColonsAtI = ip.charAt(i) == ':' && (ip.charAt(i + 1) == ':');
			if (ipContainsTwoColonsAtI && first) {
				first = false;
				inCheck = true;
			} else if (ipContainsTwoColonsAtI && !inCheck) {
				return false;
			} else if (!ipContainsTwoColonsAtI) {
				inCheck = false;
			}
		}
		return true;
	}

	private static boolean checkColonPlacement(String ip) {
		boolean ipContainsThreeColons = ip.contains(":::");
		boolean startsWithOnlyOneColon = ip.charAt(0) == ':' && ip.charAt(1) != ':';
		boolean endsWithOnlyOneColon = ip.charAt(ip.length() - 1) == ':' && ip.charAt(ip.length() - 2) != ':';
		return !ipContainsThreeColons && !startsWithOnlyOneColon && !endsWithOnlyOneColon;
	}

	private Arbitrary<String> domainDomain() {
		Arbitrary<Integer> length = Arbitraries.integers().between(0, 25);
		Arbitrary<String> lastDomainPart = domainDomainPart();
		return length.flatMap(depth -> Arbitraries.recursive(
				() -> lastDomainPart,
				this::domainDomainGenerate,
				depth
		)).filter(v -> v.length() <= 253 && validUseOfDotsInDomain(v) && validUseOfHyphensInDomain(v) && tldNotAllNumeric(v));
	}

	private boolean validUseOfDotsInDomain(String domain) {
		boolean tldMinimumTwoSigns = domain.length() < 2 || domain.charAt(domain.length() - 2) != '.';
		boolean firstSignNotADot = domain.charAt(0) != '.';
		boolean lastSignNotADot = domain.charAt(domain.length() - 1) != '.';
		boolean containsNoDoubleDot = !domain.contains("..");
		return tldMinimumTwoSigns && firstSignNotADot && lastSignNotADot && containsNoDoubleDot;
	}

	private boolean validUseOfHyphensInDomain(String domain) {
		boolean firstSignNotAHyphen = domain.charAt(0) != '-';
		boolean lastSignNotAHyphen = domain.charAt(domain.length() - 1) != '-';
		return firstSignNotAHyphen && lastSignNotAHyphen;
	}

	private boolean tldNotAllNumeric(String domain) {
		String parts[] = domain.split("\\.");
		if (parts.length == 1) {
			return true;
		}
		String tld = parts[parts.length - 1];
		for (char c : tld.toCharArray()) {
			if (c < '0' || c > '9') {
				return true;
			}
		}
		return false;
	}

	private Arbitrary<String> domainDomainGenerate(Arbitrary<String> domain) {
		return Combinators.combine(domainDomainPart(), domain).as((x, y) -> x + "." + y);
	}

	private Arbitrary<String> domainDomainPart() {
		//Not using .alpha().numeric().withChars("-") because runtime is too high
		//Using "." in withChars() to generate more subdomains
		Arbitrary<String> domain =
				Arbitraries.strings().withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-.")
						   .ofMinLength(1).ofMaxLength(63);
		return domain;
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
