package net.jqwik.engine.properties.arbitraries;

import net.jqwik.api.*;

public class DefaultEmailArbitrary extends AbstractArbitraryBase {

	public Arbitrary<String> emails(){
		Arbitrary<String> arbitraryLocalPart = emailsLocalPart();
		Arbitrary<String> arbitraryDomain = emailsDomain();
		return Combinators.combine(arbitraryLocalPart, arbitraryDomain).as((localPart, domain) -> localPart + "@" + domain);
	}

	private Arbitrary<String> emailsLocalPart(){
		Arbitrary<String> unquoted = emailsLocalPartUnquoted();
		Arbitrary<String> quoted = emailsLocalPartQuoted();
		return Arbitraries.oneOf(unquoted, quoted);
	}

	private Arbitrary<String> emailsLocalPartUnquoted(){
		Arbitrary<String> unquoted = Arbitraries.strings().alpha().numeric().withChars("!#$%&'*+-/=?^_`{|}~.").ofMinLength(1).ofMaxLength(64);
		unquoted = unquoted.filter(v -> !v.contains(".."));
		unquoted = unquoted.filter(v -> v.charAt(0) != '.');
		unquoted = unquoted.filter(v -> v.charAt(v.length() - 1) != '.');
		return unquoted;
	}

	private Arbitrary<String> emailsLocalPartQuoted(){
		Arbitrary<String> quoted = Arbitraries.strings().alpha().numeric().withChars(" !#$%&'*+-/=?^_`{|}~.\"(),:;<>@[\\]").ofMinLength(1).ofMaxLength(62);
		quoted = quoted.map(v -> "\"" + v.replace("\\", "\\\\").replace("\"", "\\\"") + "\"");
		quoted = quoted.filter(v -> v.length() <= 64);
		return quoted;
	}

	private Arbitrary<String> emailsDomain(){
		return Arbitraries.frequencyOf(
				Tuple.of(1, emailsDomainIPv4()),
				Tuple.of(1, emailsDomainIPv6()),
				Tuple.of(2, emailsDomainDomain())
		);
	}

	private Arbitrary<String> emailsDomainIPv4(){
		Arbitrary<Integer> addressPart = Arbitraries.integers().between(0, 255);
		return Combinators.combine(addressPart, addressPart, addressPart, addressPart).as((a, b, c, d) -> "[" + a + "." + b + "." + c + "." + d + "]");
	}

	private Arbitrary<String> emailsDomainIPv6(){
		Arbitrary<String> addressPart = Arbitraries.strings().numeric().withChars('a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F').ofMaxLength(4);
		Arbitrary<String> address = Combinators.combine(addressPart, addressPart, addressPart, addressPart, addressPart, addressPart, addressPart, addressPart).as((a, b, c, d, e, f, g, h) -> "[" + a + ":" + b + ":" + c + ":" + d + ":" + e + ":" + f + ":" + g + ":" + h + "]");
		address = address.filter(v -> isValidIPv6Address(v));
		return address;
	}

	private boolean isValidIPv6Address(String ip){
		ip = ip.substring(1, ip.length() - 1);
		if(ip.contains(":::") || (ip.charAt(0) == ':' && ip.charAt(1) != ':') || (ip.charAt(ip.length() - 1) == ':' && ip.charAt(ip.length() - 2) != ':')){
			return false;
		}
		boolean first = true;
		boolean inCheck = false;
		for(int i = 0; i < ip.length() - 1; i++){
			if(ip.charAt(i) == ':' && (ip.charAt(i+1) == ':')){
				if(first){
					first = false; inCheck = true;
				} else if(!inCheck){
					return false;
				}
			} else {
				inCheck = false;
			}
		}
		return true;
	}

	private Arbitrary<String> emailsDomainDomain(){
		Arbitrary<String> domain = Arbitraries.strings().numeric().alpha().ofLength(10);
		return domain;
	}

}
