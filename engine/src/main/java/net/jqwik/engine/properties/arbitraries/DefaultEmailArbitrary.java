package net.jqwik.engine.properties.arbitraries;

import net.jqwik.api.*;

public class DefaultEmailArbitrary extends ArbitraryDecorator<String> {

	@Override
	protected Arbitrary<String> arbitrary(){
		Arbitrary<String> arbitraryLocalPart = localPart();
		Arbitrary<String> arbitraryDomain = domain();
		return Combinators.combine(arbitraryLocalPart, arbitraryDomain).as((localPart, domain) -> localPart + "@" + domain);
	}

	private Arbitrary<String> localPart(){
		Arbitrary<String> unquoted = localPartUnquoted();
		Arbitrary<String> quoted = localPartQuoted();
		return Arbitraries.oneOf(unquoted, quoted);
	}

	private Arbitrary<String> localPartUnquoted(){
		Arbitrary<String> unquoted = Arbitraries.strings().alpha().numeric().withChars("!#$%&'*+-/=?^_`{|}~.").ofMinLength(1).ofMaxLength(64);
		unquoted = unquoted.filter(v -> !v.contains(".."));
		unquoted = unquoted.filter(v -> v.charAt(0) != '.');
		unquoted = unquoted.filter(v -> v.charAt(v.length() - 1) != '.');
		return unquoted;
	}

	private Arbitrary<String> localPartQuoted(){
		Arbitrary<String> quoted = Arbitraries.strings().alpha().numeric().withChars(" !#$%&'*+-/=?^_`{|}~.\"(),:;<>@[\\]").ofMinLength(1).ofMaxLength(62);
		quoted = quoted.map(v -> "\"" + v.replace("\\", "\\\\").replace("\"", "\\\"") + "\"");
		quoted = quoted.filter(v -> v.length() <= 64);
		return quoted;
	}

	private Arbitrary<String> domain(){
		return Arbitraries.frequencyOf(
				Tuple.of(2, domainDomain()),
				Tuple.of(1, domainIPv4()),
				Tuple.of(1, domainIPv6())
		);
	}

	private Arbitrary<String> domainIPv4(){
		Arbitrary<Integer> addressPart = Arbitraries.integers().between(0, 255);
		return Combinators.combine(addressPart, addressPart, addressPart, addressPart).as((a, b, c, d) -> "[" + a + "." + b + "." + c + "." + d + "]");
	}

	private Arbitrary<String> domainIPv6(){
		Arbitrary<String> addressPart = Arbitraries.strings().numeric().withChars('a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F').ofMaxLength(4);
		Arbitrary<String> address = Combinators.combine(addressPart, addressPart, addressPart, addressPart, addressPart, addressPart, addressPart, addressPart).as((a, b, c, d, e, f, g, h) -> "[" + a + ":" + b + ":" + c + ":" + d + ":" + e + ":" + f + ":" + g + ":" + h + "]");
		address = address.filter(v -> validUseOfColonInIPv6Address(v.substring(1, v.length() - 1)));
		return address;
	}

	// TODO: Cyclomatic Complexity > 11
	public static boolean validUseOfColonInIPv6Address(String ip){
		boolean ipContainsThreeColons = ip.contains(":::");
		boolean startsWithOnlyOneColon = ip.charAt(0) == ':' && ip.charAt(1) != ':';
		boolean endsWithOnlyOneColon = ip.charAt(ip.length() - 1) == ':' && ip.charAt(ip.length() - 2) != ':';
		if(ipContainsThreeColons || startsWithOnlyOneColon || endsWithOnlyOneColon){
			return false;
		}
		boolean first = true;
		boolean inCheck = false;
		for(int i = 0; i < ip.length() - 1; i++){
			boolean ipContainsTwoColonsAtI = ip.charAt(i) == ':' && (ip.charAt(i+1) == ':');
			if(ipContainsTwoColonsAtI){
				if(first){
					first = false;
					inCheck = true;
				} else if(!inCheck){
					return false;
				}
			} else {
				inCheck = false;
			}
		}
		return true;
	}

	private Arbitrary<String> domainDomain(){
		Arbitrary<Integer> length = Arbitraries.integers().between(0, 25);
		Arbitrary<String> lastDomainPart = domainDomainPart();
		return length.flatMap(depth -> Arbitraries.recursive(
				() -> lastDomainPart,
				this::domainDomainGenerate,
				depth
		)).filter(v -> v.length() <= 253 && validUseOfDotsInDomain(v) && validUseOfHyphensInDomain(v));
	}

	private boolean validUseOfDotsInDomain(String domain){
		boolean tldMinimumTwoSigns = domain.length() < 2 || domain.charAt(domain.length() - 2) != '.';
		boolean firstSignNotADot = domain.charAt(0) != '.';
		boolean lastSignNotADot = domain.charAt(domain.length() - 1) != '.';
		boolean containsNoDoubleDot = !domain.contains("..");
		return tldMinimumTwoSigns && firstSignNotADot && lastSignNotADot && containsNoDoubleDot;
	}

	private boolean validUseOfHyphensInDomain(String domain){
		boolean firstSignNotAHyphen = domain.charAt(0) != '-';
		boolean lastSignNotAHyphen = domain.charAt(domain.length() - 1) != '-';
		return firstSignNotAHyphen && lastSignNotAHyphen;
	}

	private Arbitrary<String> domainDomainGenerate(Arbitrary<String> domain){
		return Combinators.combine(domainDomainPart(), domain).as((x, y) -> x + "." + y);
	}

	private Arbitrary<String> domainDomainPart(){
		//Not using .alpha().numeric().withChars("-") because runtime is too high
		//Using "." in withChars() to generate more subdomains
		Arbitrary<String> domain = Arbitraries.strings().withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-.").ofMinLength(1).ofMaxLength(63);
		return domain;
	}

}
