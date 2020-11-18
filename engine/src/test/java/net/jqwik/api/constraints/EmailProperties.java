package net.jqwik.api.constraints;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ArbitrariesEmailsTests.*;

public class EmailProperties {

	@Property
	void onlyIPAddressesAreGenerated(@ForAll @Email(domains = false) String email){
		String domain = getDomainOfEmail(email);
		assertThat(isIPAddress(domain)).isTrue();
	}

	@Property
	void onlyIPv4AddressesAreGenerated(@ForAll @Email(domains = false, ipv6Addresses = false) String email){
		String domain = getDomainOfEmail(email);
		assertThat(isIPAddress(domain)).isTrue();
		assertThat(domain).contains(".");
	}

	@Property
	void onlyIPv6AddressesAreGenerated(@ForAll @Email(domains = false, ipv4Addresses = false) String email){
		String domain = getDomainOfEmail(email);
		assertThat(isIPAddress(domain)).isTrue();
		assertThat(domain).contains(":");
	}

	@Property
	void onlyDomainsAreGenerated(@ForAll @Email(ipv6Addresses = false, ipv4Addresses = false) String email){
		String domain = getDomainOfEmail(email);
		assertThat(isIPAddress(domain)).isFalse();
	}

	@Property
	void onlyQuotedLocalPartsAreGenerated(@ForAll @Email(unquotedLocalPart = false) String email){
		String localPart = getLocalPartOfEmail(email);
		assertThat(isQuoted(localPart)).isTrue();
	}

	@Property
	void onlyUnquotedLocalPartsAreGenerated(@ForAll @Email(quotedLocalPart = false) String email){
		String localPart = getLocalPartOfEmail(email);
		assertThat(isQuoted(localPart)).isFalse();
	}

}
