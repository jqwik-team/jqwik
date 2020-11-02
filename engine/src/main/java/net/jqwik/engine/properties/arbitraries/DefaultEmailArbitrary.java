package net.jqwik.engine.properties.arbitraries;

import net.jqwik.api.*;

public class DefaultEmailArbitrary extends AbstractArbitraryBase {

	public Arbitrary<String> emails(){
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
				Tuple.of(1, domainIPv4()),
				Tuple.of(1, domainIPv6()),
				Tuple.of(2, domainDomain())
		);
	}

	private Arbitrary<String> domainIPv4(){
		Arbitrary<Integer> addressPart = Arbitraries.integers().between(0, 255);
		return Combinators.combine(addressPart, addressPart, addressPart, addressPart).as((a, b, c, d) -> "[" + a + "." + b + "." + c + "." + d + "]");
	}

	private Arbitrary<String> domainIPv6(){
		Arbitrary<String> addressPart = Arbitraries.strings().numeric().withChars('a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F').ofMaxLength(4);
		Arbitrary<String> address = Combinators.combine(addressPart, addressPart, addressPart, addressPart, addressPart, addressPart, addressPart, addressPart).as((a, b, c, d, e, f, g, h) -> "[" + a + ":" + b + ":" + c + ":" + d + ":" + e + ":" + f + ":" + g + ":" + h + "]");
		address = address.filter(v -> generatedAddressIsValidIPv6Address(v));
		return address;
	}

	private boolean generatedAddressIsValidIPv6Address(String ip){
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

	private Arbitrary<String> domainDomain(){
		return Arbitraries.lazyOf(
				() -> domainDomainPart(),
				this::domainDomainGenerate
		).filter(v -> v.length() <= 253).filter(v -> (v.length() < 2 || v.charAt(v.length() - 2) != '.') && v.charAt(0) != '.' && v.charAt(v.length() - 1) != '.' && !v.contains("..")).filter(v -> v.charAt(0) != '-' && v.charAt(v.length() - 1) != '-');
	}

	private Arbitrary<String> domainDomainGenerate(){
		return Combinators.combine(domainDomain(), domainDomainPart()).as((x, y) -> x + "." + y);
	}

	private Arbitrary<String> domainDomainPart(){
		//Not using .alpha().numeric().withChars("-") because runtime is too high
		//Using "." in withChars() to generate more subdomains
		Arbitrary<String> domain = Arbitraries.strings().withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-.").ofMinLength(1).ofMaxLength(63);
		return domain;
	}

}
