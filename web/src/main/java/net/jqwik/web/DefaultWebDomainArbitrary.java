package net.jqwik.web;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultWebDomainArbitrary extends ArbitraryDecorator<String> implements Arbitrary<String> {

	@Override
	protected Arbitrary<String> arbitrary() {
		Arbitrary<String> topLevelDomain = topLevelDomain();
		Arbitrary<String> subDomains = domainPart(1, 63).list()
														.ofMinSize(1).ofMaxSize(10)
														.map(list -> String.join(".", list));

		return Combinators.combine(subDomains, topLevelDomain)
						  .as((sd, tld) -> sd + "." + tld)
						  .filter(v -> v.length() < 253)
						  .edgeCases(stringConfig -> stringConfig.includeOnly("a.aa", "0.aa"));

	}

	private Arbitrary<String> topLevelDomain() {
		return domainPart(2, 10).filter(this::doesNotStartWithDigit);
	}

	private boolean doesNotStartWithDigit(String tld) {
		return !Character.isDigit(tld.charAt(0));
	}

	private Arbitrary<String> domainPart(int minLength, int maxLength) {
		return Arbitraries.strings()
						  .withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
						  .withChars("0123456789-")
						  .ofMinLength(minLength).ofMaxLength(maxLength)
						  .filter(this::validUseOfHyphensInDomainPart);
	}

	private boolean validUseOfHyphensInDomainPart(String domainPart) {
		boolean firstSignNotAHyphen = domainPart.charAt(0) != '-';
		if (firstSignNotAHyphen) {
			boolean lastSignNotAHyphen = domainPart.charAt(domainPart.length() - 1) != '-';
			return lastSignNotAHyphen;
		}
		return false;
	}

}
