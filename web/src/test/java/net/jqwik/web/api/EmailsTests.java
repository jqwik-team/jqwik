package net.jqwik.web.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;
import static net.jqwik.web.api.EmailTestingSupport.*;

@Group
@PropertyDefaults(edgeCases = EdgeCasesMode.MIXIN)
public class EmailsTests {

	@Group
	@PropertyDefaults(tries = 100)
	class AllGeneratedEmailAddressesAreValid {

		@Property(tries = 10)
		void containsAt(@ForAll("emails") String email) {
			assertThat(email).contains("@");
		}

		@Property
		void validLengthBeforeAt(@ForAll("emails") String email) {
			String localPart = getLocalPartOfEmail(email);
			assertThat(localPart.length()).isBetween(1, 64);
		}

		@Property
		void validLengthAfterAt(@ForAll("emails") String email) {
			String domain = getEmailHost(email);
			assertThat(domain.length()).isBetween(1, 253);
		}

		@Property
		void validCharsBeforeAtUnquoted(@ForAll("emails") String email) {
			String localPart = getLocalPartOfEmail(email);
			Assume.that(!isQuoted(localPart));
			assertThat(localPart.chars()).allMatch(c -> isIn(c, ALLOWED_CHARS_LOCALPART_UNQUOTED));
		}

		@Property
		void validCharsBeforeAtQuoted(@ForAll("onlyQuoted") String email) {
			String localPart = getLocalPartOfEmail(email);
			Assume.that(isQuoted(localPart));
			assertThat(localPart.chars()).allMatch(c -> isIn(c, ALLOWED_CHARS_LOCALPART_QUOTED));
		}

		@Property
		void validUseOfQuotedBackslashAndQuotationMarks(@ForAll("onlyQuoted") String email) {
			String localPart = getLocalPartOfEmail(email);
			Assume.that(isQuoted(localPart));
			localPart = localPart.substring(1, localPart.length() - 1);
			localPart = localPart.replace("\\\"", "").replace("\\\\", "");
			assertThat(localPart).doesNotContain("\\");
			assertThat(localPart).doesNotContain("\"");
		}

		@Property
		void validUseOfDotBeforeAt(@ForAll("emails") String email) {
			String localPart = getLocalPartOfEmail(email);
			Assume.that(!isQuoted(localPart));
			assertThat(localPart).doesNotContain("..");
			assertThat(localPart.charAt(0)).isNotEqualTo('.');
			assertThat(localPart.charAt(localPart.length() - 1)).isNotEqualTo('.');
		}

		@Property(edgeCases = EdgeCasesMode.NONE)
		void validCharsAfterAt(@ForAll("emails") String email) {
			String domain = getEmailHost(email);
			Assume.that(!isIPAddress(domain));
			assertThat(domain.chars()).allMatch(c -> isIn(c, ALLOWED_CHARS_DOMAIN));
		}

		@Property
		void validUseOfHyphenAndDotAfterAt(@ForAll("emails") String email) {
			String domain = getEmailHost(email);
			System.out.println(domain);
			Assume.that(!isIPAddress(domain));
			assertThat(domain.charAt(0)).isNotEqualTo('-');
			assertThat(domain.charAt(domain.length() - 1)).isNotEqualTo('-');
			assertThat(domain).contains(".");
			assertThat(domain).doesNotContain("..");
			assertThat(domain.charAt(0)).isNotEqualTo('.');
			assertThat(domain.charAt(domain.length() - 1)).isNotEqualTo('.');
			Assume.that(domain.length() >= 2);
			assertThat(domain.charAt(domain.length() - 2)).isNotEqualTo('.');
		}

		@Property
		void validMaxDomainLengthAfterAt(@ForAll("emails") String email) {
			String domain = getEmailHost(email);
			Assume.that(!isIPAddress(domain));
			String[] domainParts = domain.split("\\.");
			IntStream.range(0, domainParts.length).forEach(i -> {
				assertThat(domainParts[i].length()).isLessThanOrEqualTo(63);
			});
		}

		@Property(tries = 1000)
		void tldNotAllNumeric(@ForAll("emails") String email) {
			String domain = getEmailHost(email);
			Assume.that(!isIPAddress(domain));
			String[] domainParts = domain.split("\\.");
			Assume.that(domainParts.length >= 2);
			String tld = domainParts[domainParts.length - 1];
			assertThat(containsAtLeastOneOf(tld, ALLOWED_NOT_NUMERIC_CHARS_TLD)).isTrue();
		}

		@Property(tries = 1000)
		void validIPAddressAfterAt(@ForAll("withIPAddresses") String email) {
			String domain = getEmailHost(email);
			Assume.that(isIPAddress(domain));
			String ipAddress = extractIPAddress(domain);
			assertThat(isValidIPAddress(ipAddress)).isTrue();
		}

		private boolean isIn(int c, String string) {
			return string.contains(String.valueOf((char) c));
		}

	}

	@Group
	@PropertyDefaults(tries = 100)
	class CheckEmailArbitraryMethods {

		@Property
		void byDefaultOnlyDomainsAreGenerated(@ForAll("emails") String email) {
			String domain = getEmailHost(email);
			assertThat(isIPAddress(domain)).isFalse();
		}

		@Property
		void byDefaultOnlyUnquotedLocalPartsAreGenerated(@ForAll("emails") String email) {
			String localPart = getLocalPartOfEmail(email);
			assertThat(isQuoted(localPart)).isFalse();
		}

		@Property
		void ipAddressesAreGenerated(@ForAll("withIPAddresses") String email) {
			String domain = getEmailHost(email);
			Assume.that(isIPAddress(domain));
			assertThat(isValidIPv4Address(domain) || isValidIPv6Address(domain));
		}

		@Property(maxDiscardRatio = 10)
		void ipv4AddressesAreGenerated(@ForAll("withIPv4Addresses") String email) {
			String domain = getEmailHost(email);
			Assume.that(isIPAddress(domain));
			assertThat(domain).contains(".");
		}

		@Provide
		private EmailArbitrary withIPv4Addresses() {
			return Web.emails().allowIpv4Host();
		}

		@Property(maxDiscardRatio = 10)
		void ipv6AddressesAreGenerated(@ForAll("withIPv6Addresses") String email) {
			String domain = getEmailHost(email);
			Assume.that(isIPAddress(domain));
			assertThat(domain).contains(":");
		}

		@Provide
		private EmailArbitrary withIPv6Addresses() {
			return Web.emails().allowIpv6Host();
		}

		@Property(maxDiscardRatio = 10)
		void quotedLocalPartsAreGenerated(@ForAll("withQuoted") String email) {
			String localPart = getLocalPartOfEmail(email);
			Assume.that(localPart.startsWith("\""));
			assertThat(isQuoted(localPart)).isTrue();
		}

	}

	@Group
	class CheckAllVariantsAreCovered {

		@Property
		void quotedAndUnquotedUsernamesAreGenerated(@ForAll("withQuoted") String email) {
			String localPart = getLocalPartOfEmail(email);
			Statistics.label("Quoted usernames")
					  .collect(isQuoted(localPart))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p > 10);
						  coverage.check(false).percentage(p -> p > 70);
					  });
		}

		@Property
		void domainsAndIpHostsAreGenerated(@ForAll("withIPAddresses") String email) {
			String domain = getEmailHost(email);
			Statistics.label("Domains")
					  .collect(isIPAddress(domain))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p > 10);
						  coverage.check(false).percentage(p -> p > 50);
					  });
		}

		@Property
		void Ipv4AndIpv6HostsAreGenerated(@ForAll("withIPAddresses") String email) {
			String domain = getEmailHost(email);
			Assume.that(isIPAddress(domain));
			String address = extractIPAddress(domain);
			Statistics.label("IPv4 addresses")
					  .collect(address.contains("."))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p > 35);
						  coverage.check(false).percentage(p -> p > 35);
					  });
		}

		@Property
		void domainHostsWithTwoAndMorePartsAreGenerated(@ForAll("emails") String email) {
			String domain = getEmailHost(email);
			Assume.that(!isIPAddress(domain));
			int domainParts = (int) (domain.chars().filter(v -> v == '.').count() + 1);
			Statistics.label("Domain parts")
					  .collect(domainParts)
					  .coverage(coverage -> {
						  coverage.check(2).count(c -> c >= 1);
						  coverage.check(3).count(c -> c >= 1);
						  coverage.check(4).count(c -> c >= 1);
						  coverage.check(5).count(c -> c >= 1);
						  coverage.check(6).count(c -> c >= 1);
					  });
		}

	}

	@Group
	@PropertyDefaults(tries = 10)
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			EmailArbitrary emails = Web.emails();
			String value = falsifyThenShrink(emails.generator(1000), random, TestingFalsifier.alwaysFalsify());
			assertThat(value).isEqualTo("a@a.aa");
		}

		@Property
		void domainShrinking(@ForAll Random random) {
			EmailArbitrary emails = Web.emails();
			Falsifier<String> falsifier = falsifyDomain();
			String value = falsifyThenShrink(emails.generator(1000), random, falsifier);
			assertThat(value).isEqualTo("a@a.aa");
		}

		@Property
		void ipv4Shrinking(@ForAll Random random) {
			EmailArbitrary emails = Web.emails().allowIpv4Host();
			Falsifier<String> falsifier = falsifyIPv4();
			String value = falsifyThenShrink(emails.generator(1000), random, falsifier);
			assertThat(value).isEqualTo("a@[0.0.0.0]");
		}

		@Property
		void ipv6Shrinking(@ForAll Random random) {
			Arbitrary<String> emails = Web.emails().allowIpv6Host();
			Falsifier<String> falsifier = falsifyIPv6();
			String value = falsifyThenShrink(emails.generator(1000), random, falsifier);
			assertThat(value).isEqualTo("a@[::]");
		}

		private TestingFalsifier<String> falsifyDomain() {
			return email -> {
				String domain = getEmailHost(email);
				return isIPAddress(domain);
			};
		}

		private TestingFalsifier<String> falsifyIPv4() {
			return email -> {
				String domain = getEmailHost(email);
				return !(isIPAddress(domain) && domain.contains("."));
			};
		}

		private TestingFalsifier<String> falsifyIPv6() {
			return email -> {
				String domain = getEmailHost(email);
				return !(isIPAddress(domain) && domain.contains(":"));
			};
		}

	}

	@Group
	class EdgeCasesTests {

		@Example
		void all() {
			EmailArbitrary emails = Web.emails().allowQuotedLocalPart().allowIpv4Host().allowIpv6Host();
			int expectedNumberOfEdgeCases = (3 + 2) * (2 + 3 + 4);
			Set<String> allEdgeCases = collectEdgeCaseValues(emails.edgeCases(1000));
			assertThat(allEdgeCases).hasSize(expectedNumberOfEdgeCases);
		}

		@Example
		void unquotedLocalPart() {
			EmailArbitrary emails = Web.emails();
			Set<String> localParts = collectEdgeCaseValues(emails.edgeCases())
											 .stream()
											 .map(EmailTestingSupport::getLocalPartOfEmail)
											 .collect(Collectors.toSet());

			assertThat(localParts).containsExactlyInAnyOrder("a", "A", "0");
		}

		@Example
		void quotedLocalPart() {
			Arbitrary<String> emails = Web.emails().allowQuotedLocalPart().filter(email -> email.startsWith("\""));
			Set<String> localParts = collectEdgeCaseValues(emails.edgeCases())
											 .stream()
											 .map(EmailTestingSupport::getLocalPartOfEmail)
											 .collect(Collectors.toSet());

			assertThat(localParts).containsExactlyInAnyOrder("\"a\"", "\" \"");
		}

		@Example
		void domainHost() {
			EmailArbitrary emails = Web.emails();
			Set<String> hosts = collectEdgeCaseValues(emails.edgeCases())
											 .stream()
											 .map(EmailTestingSupport::getEmailHost)
											 .collect(Collectors.toSet());

			assertThat(hosts).containsExactlyInAnyOrder("a.aa", "0.aa");
		}

		@Example
		void ipv4Host() {
			Arbitrary<String> emails = Web.emails().allowIpv4Host();
			Set<String> hosts = collectEdgeCaseValues(emails.edgeCases())
											 .stream()
											 .map(EmailTestingSupport::getEmailHost)
											 .collect(Collectors.toSet());

			assertThat(hosts).contains(
					"[0.0.0.0]", "[255.255.255.255]", "[127.0.0.1]"
			);
		}

		@Example
		void ipv6Host() {
			EmailArbitrary emails = Web.emails().allowIpv6Host();
			Set<String> hosts = collectEdgeCaseValues(emails.edgeCases())
											 .stream()
											 .map(EmailTestingSupport::getEmailHost)
											 .collect(Collectors.toSet());

			assertThat(hosts).contains(
					"[::]",
					"[0:0:0:0:0:0:0:0]",
					"[FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF]", "[ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff]"
			);
		}

	}

	@Provide
	private EmailArbitrary emails() {
		return Web.emails();
	}

	@Provide
	private EmailArbitrary withQuoted() {
		return Web.emails().allowQuotedLocalPart();
	}

	@Provide
	private Arbitrary<String> onlyQuoted() {
		return withQuoted().filter(email -> email.startsWith("\""));
	}

	@Provide
	private EmailArbitrary withIPAddresses() {
		return Web.emails().allowIpv4Host().allowIpv6Host();
	}

}
