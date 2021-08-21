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
		void byDefaultOnlyStandardAddressesAreGenerated(@ForAll @Email String email) {
			assertThat(isQuoted(getLocalPartOfEmail(email))).isFalse();
			assertThat(isValidDomain(getEmailHost(email))).isTrue();
		}

		@Property
		void ipAddressesAreGenerated(@ForAll @Email(ipv4Host = true, ipv6Host = true) String email) {
			Assume.that(isIPAddress(getEmailHost(email)));
			String ipAddress = extractIPAddress(getEmailHost(email));
			assertThat(isValidIPAddress(ipAddress)).isTrue();
		}

		@Property
		void ipv4AddressesAreGenerated(@ForAll @Email(ipv4Host = true) String email) {
			Assume.that(isIPAddress(getEmailHost(email)));
			String ipAddress = extractIPAddress(getEmailHost(email));
			assertThat(isValidIPv4Address(getEmailHost(ipAddress))).isTrue();
		}

		@Property
		void ipv6AddressesAreGenerated(@ForAll @Email(ipv6Host = true) String email) {
			Assume.that(isIPAddress(getEmailHost(email)));
			String ipAddress = extractIPAddress(getEmailHost(email));
			assertThat(isValidIPv6Address(getEmailHost(ipAddress))).isTrue();
		}

		@Property
		void quotedLocalPartsAreGenerated(@ForAll @Email(quotedLocalPart = true) String email) {
			String localPart = getLocalPartOfEmail(email);
			Assume.that(isQuoted(localPart));
			assertThat(localPart).startsWith("\"").endsWith("\"");
		}

		@Property
		@ExpectFailure(checkResult = ShrinkToAatAdotAA.class)
		boolean shrinking(@ForAll @Email String email) {
			return false;
		}

		class ShrinkToAatAdotAA extends ShrinkToChecker {
			@Override
			public Iterable<?> shrunkValues() {
				return Arrays.asList("a@a.aa");
			}
		}

	}

	@Group
	@StatisticsReport(onFailureOnly = true)
	class CheckAllVariantsAreCovered {

		@Property
		void quotedAndUnquotedUsernamesAreGenerated(@ForAll @Email(quotedLocalPart = true) String email) {
			String localPart = getLocalPartOfEmail(email);
			Statistics.label("Quoted usernames")
					  .collect(isQuoted(localPart))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p > 10);
						  coverage.check(false).percentage(p -> p > 70);
					  });
		}

		@Property
		void domainsAndIPAddressesAreGenerated(@ForAll @Email(ipv4Host = true, ipv6Host = true) String email) {
			String domain = getEmailHost(email);
			Statistics.label("Domains")
					  .collect(isIPAddress(domain))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p > 10);
						  coverage.check(false).percentage(p -> p > 50);
					  });
		}

		@Property
		void IPv4AndIPv6AreGenerated(@ForAll @Email(ipv6Host = true, ipv4Host = true) String email) {
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
