package net.jqwik.api.constraints;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ArbitrariesEmailsTests.*;

@PropertyDefaults(edgeCases = EdgeCasesMode.NONE)
@Group
class EmailProperties {

	@Group
	class checkAnnotationProperties {

		@Property
		void onlyIPAddressesAreGenerated(@ForAll @Email(domains = false) String email) {
			String domain = getDomainOfEmail(email);
			assertThat(isIPAddress(domain)).isTrue();
		}

		@Property
		void onlyIPv4AddressesAreGenerated(@ForAll @Email(domains = false, ipv6Addresses = false) String email) {
			String domain = getDomainOfEmail(email);
			assertThat(isIPAddress(domain)).isTrue();
			assertThat(domain).contains(".");
		}

		@Property
		void onlyIPv6AddressesAreGenerated(@ForAll @Email(domains = false, ipv4Addresses = false) String email) {
			String domain = getDomainOfEmail(email);
			assertThat(isIPAddress(domain)).isTrue();
			assertThat(domain).contains(":");
		}

		@Property
		void onlyDomainsAreGenerated(@ForAll @Email(ipv6Addresses = false, ipv4Addresses = false) String email) {
			String domain = getDomainOfEmail(email);
			assertThat(isIPAddress(domain)).isFalse();
		}

		@Property
		void onlyQuotedLocalPartsAreGenerated(@ForAll @Email(unquotedLocalPart = false) String email) {
			String localPart = getLocalPartOfEmail(email);
			assertThat(isQuoted(localPart)).isTrue();
		}

		@Property
		void onlyUnquotedLocalPartsAreGenerated(@ForAll @Email(quotedLocalPart = false) String email) {
			String localPart = getLocalPartOfEmail(email);
			assertThat(isQuoted(localPart)).isFalse();
		}

	}

	@Group
	class CheckAllVariantsAreCovered {

		@Property
		void quotedAndUnquotedUsernamesAreGenerated(@ForAll @Email String email) {
			String localPart = getLocalPartOfEmail(email);
			Statistics.label("Quoted usernames")
					  .collect(isQuoted(localPart))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p > 35);
						  coverage.check(false).percentage(p -> p > 35);
					  });
		}

		@Property
		void domainsAndIPAddressesAreGenerated(@ForAll @Email String email) {
			String domain = getDomainOfEmail(email);
			Statistics.label("Domains")
					  .collect(isIPAddress(domain))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p > 35);
						  coverage.check(false).percentage(p -> p > 35);
					  });
		}

		@Property
		void IPv4AndIPv6AreGenerated(@ForAll @Email String email) {
			String domain = getDomainOfEmail(email);
			Assume.that(isIPAddress(domain));
			domain = domain.substring(1, domain.length() - 1);
			Statistics.label("IPv4 addresses")
					  .collect(domain.contains("."))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p > 35);
						  coverage.check(false).percentage(p -> p > 35);
					  });
		}

	}

}
