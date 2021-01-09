package net.jqwik.web.api;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.web.api.EmailTestingSupport.*;

@Group
class EmailProperties {

	@Group
	class CheckAnnotationProperties {

		@Property
		void onlyIPAddressesAreGenerated(@ForAll @Email(domainHost = false) String email) {
			String ipAddress = extractIPAddress(getEmailHost(email));
			assertThat(isValidIPAddress(ipAddress)).isTrue();
		}

		@Property
		void onlyIPv4AddressesAreGenerated(@ForAll @Email(domainHost = false, ipv6Host = false) String email) {
			String ipAddress = extractIPAddress(getEmailHost(email));
			assertThat(isValidIPv4Address(getEmailHost(ipAddress))).isTrue();
		}

		@Property
		void onlyIPv6AddressesAreGenerated(@ForAll @Email(domainHost = false, ipv4Host = false) String email) {
			String ipAddress = extractIPAddress(getEmailHost(email));
			assertThat(isValidIPv6Address(getEmailHost(ipAddress))).isTrue();
		}

		@Property
		void onlyDomainsAreGenerated(@ForAll @Email(ipv6Host = false, ipv4Host = false) String email) {
			String domain = getEmailHost(email);
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

		@Property
		@ExpectFailure(failureType = JqwikException.class)
		void generationFailsWithNoLocalPart(@ForAll @Email(quotedLocalPart = false, unquotedLocalPart = false) String email) {
		}

		@Property
		@ExpectFailure(failureType = JqwikException.class)
		void generationFailsWithNoHost(@ForAll @Email(domainHost = false, ipv4Host = false, ipv6Host = false) String email) {
		}

		@Property
		@ExpectFailure(checkResult = ShrinkToAatAdotAA.class)
		boolean shrinking(@ForAll @Email(ipv6Host = false, ipv4Host = false, quotedLocalPart = false) String email) {
			return false;
		}

		class ShrinkToAatAdotAA extends ShrinkToChecker {
			@Override
			public Iterable<?> shrunkValues() {
				return Arrays.asList("A@a.aa");
			}
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
						  coverage.check(true).percentage(p -> p > 10);
						  coverage.check(false).percentage(p -> p > 70);
					  });
		}

		@Property
		void domainsAndIPAddressesAreGenerated(@ForAll @Email String email) {
			String domain = getEmailHost(email);
			Statistics.label("Domains")
					  .collect(isIPAddress(domain))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p > 10);
						  coverage.check(false).percentage(p -> p > 50);
					  });
		}

		@Property
		void IPv4AndIPv6AreGenerated(@ForAll @Email String email) {
			String domain = getEmailHost(email);
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
