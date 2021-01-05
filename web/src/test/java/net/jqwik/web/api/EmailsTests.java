package net.jqwik.web.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;
import net.jqwik.web.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
public class EmailsTests {

	private static final String ALLOWED_CHARS_DOMAIN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-.";
	private static final String ALLOWED_CHARS_LOCALPART_UNQUOTED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.!#$%&'*+-/=?^_`{|}~";
	private static final String ALLOWED_CHARS_LOCALPART_QUOTED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.!#$%&'*+-/=?^_`{|}~\"(),:;<>@[\\] ";
	private static final String ALLOWED_CHARS_IPV6_ADDRESS = "0123456789abcdefABCDEF";
	private static final String ALLOWED_NOT_NUMERIC_CHARS_TLD = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-";

	@Group
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
			assertThat(localPart.chars()).allMatch(c -> stringContainsChar(ALLOWED_CHARS_LOCALPART_UNQUOTED, c));
		}

		@Property
		void validCharsBeforeAtQuoted(@ForAll("emails") String email) {
			String localPart = getLocalPartOfEmail(email);
			Assume.that(isQuoted(localPart));
			assertThat(localPart.chars()).allMatch(c -> stringContainsChar(ALLOWED_CHARS_LOCALPART_QUOTED, c));
		}

		@Property
		void validUseOfQuotedBackslashAndQuotationMarks(@ForAll("emails") String email) {
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

		@Property
		void validCharsAfterAt(@ForAll("emails") String email) {
			String domain = getEmailHost(email);
			Assume.that(!isIPAddress(domain));
			assertThat(domain.chars()).allMatch(c -> stringContainsChar(ALLOWED_CHARS_DOMAIN, c));
		}

		@Property
		void validUseOfHyphenAndDotAfterAt(@ForAll("emails") String email) {
			String domain = getEmailHost(email);
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

		@Property(tries = 5000)
		void tldNotAllNumeric(@ForAll("emails") String email) {
			String domain = getEmailHost(email);
			Assume.that(!isIPAddress(domain));
			String[] domainParts = domain.split("\\.");
			Assume.that(domainParts.length >= 2);
			String tld = domainParts[domainParts.length - 1];
			assertThat(stringContainsMinimumOneChar(ALLOWED_NOT_NUMERIC_CHARS_TLD, tld)).isTrue();
		}

		@Property(tries = 5000)
		void validIPAddressAfterAt(@ForAll("emails") String email) {
			String domain = getEmailHost(email);
			Assume.that(isIPAddress(domain));
			domain = domain.substring(1, domain.length() - 1);
			assertThat(isValidIPAddress(domain)).isTrue();
		}

		private boolean stringContainsChar(String string, int c) {
			return string.contains(Character.toString((char) c));
		}

		private boolean stringContainsMinimumOneChar(String string, String chars) {
			for (char c : chars.toCharArray()) {
				if (stringContainsChar(string, c)) {
					return true;
				}
			}
			return false;
		}

	}

	@Group
	class CheckEmailArbitraryMethods {

		@Property
		void onlyIPAddressesAreGenerated(@ForAll("onlyIPAddresses") String email) {
			String domain = getEmailHost(email);
			assertThat(isIPAddress(domain)).isTrue();
		}

		@Provide
		private EmailArbitrary onlyIPAddresses() {
			return Emails.emails().ipv4Host().ipv6Host();
		}

		@Property
		void onlyIPv4AddressesAreGenerated(@ForAll("onlyIPv4Addresses") String email) {
			String domain = getEmailHost(email);
			assertThat(isIPAddress(domain)).isTrue();
			assertThat(domain).contains(".");
		}

		@Provide
		private EmailArbitrary onlyIPv4Addresses() {
			return Emails.emails().ipv4Host();
		}

		@Property
		void onlyIPv6AddressesAreGenerated(@ForAll("onlyIPv6Addresses") String email) {
			String domain = getEmailHost(email);
			assertThat(isIPAddress(domain)).isTrue();
			assertThat(domain).contains(":");
		}

		@Provide
		private EmailArbitrary onlyIPv6Addresses() {
			return Emails.emails().ipv6Host();
		}

		@Property
		void onlyDomainsAreGenerated(@ForAll("onlyDomains") String email) {
			String domain = getEmailHost(email);
			assertThat(isIPAddress(domain)).isFalse();
		}

		@Provide
		private EmailArbitrary onlyDomains() {
			return Emails.emails().domainHost();
		}

		@Property
		void onlyQuotedLocalPartsAreGenerated(@ForAll("onlyQuoted") String email) {
			String localPart = getLocalPartOfEmail(email);
			assertThat(isQuoted(localPart)).isTrue();
		}

		@Provide
		private EmailArbitrary onlyQuoted() {
			return Emails.emails().quotedLocalPart();
		}

		@Property
		void onlyUnquotedLocalPartsAreGenerated(@ForAll("onlyUnquoted") String email) {
			String localPart = getLocalPartOfEmail(email);
			assertThat(isQuoted(localPart)).isFalse();
		}

		@Provide
		private EmailArbitrary onlyUnquoted() {
			return Emails.emails().unquotedLocalPart();
		}

	}

	@Group
	class CheckAllVariantsAreCovered {

		@Property
		void quotedAndUnquotedUsernamesAreGenerated(@ForAll("emails") String email) {
			String localPart = getLocalPartOfEmail(email);
			Statistics.label("Quoted usernames")
					  .collect(isQuoted(localPart))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p > 10);
						  coverage.check(false).percentage(p -> p > 70);
					  });
		}

		@Property
		void domainsAndIpHostsAreGenerated(@ForAll("emails") String email) {
			String domain = getEmailHost(email);
			Statistics.label("Domains")
					  .collect(isIPAddress(domain))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p > 10);
						  coverage.check(false).percentage(p -> p > 50);
					  });
		}

		@Property
		void Ipv4AndIpv6HostsAreGenerated(@ForAll("emails") String email) {
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
			EmailArbitrary emails = Emails.emails();
			String value = shrinkToMinimal(emails, random);
			assertThat(value).isEqualTo("A@a.aa");
		}

		@Property
		void domainShrinking(@ForAll Random random) {
			EmailArbitrary emails = Emails.emails();
			Falsifier<String> falsifier = falsifyDomain();
			String value = shrinkToMinimal(emails, random, falsifier);
			assertThat(value).isEqualTo("A@a.aa");
		}

		@Property
		void ipv4Shrinking(@ForAll Random random) {
			EmailArbitrary emails = Emails.emails();
			Falsifier<String> falsifier = falsifyIPv4();
			String value = shrinkToMinimal(emails, random, falsifier);
			assertThat(value).isEqualTo("A@[0.0.0.0]");
		}

		@Property
		void ipv6Shrinking(@ForAll Random random) {
			Arbitrary<String> emails = Emails.emails();
			Falsifier<String> falsifier = falsifyIPv6();
			String value = shrinkToMinimal(emails, random, falsifier);
			assertThat(value).isEqualTo("A@[::]");
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
			EmailArbitrary emails = Emails.emails();
			int expectedNumberOfEdgeCases = (4 + 3) * (2 + 3 + 4);
			Set<String> allEdgeCases = collectEdgeCases(emails.edgeCases());
			assertThat(allEdgeCases).hasSize(expectedNumberOfEdgeCases);

			// allEdgeCases.forEach(System.out::println);
		}

		@Example
		void unquotedLocalPart() {
			EmailArbitrary emails = Emails.emails().unquotedLocalPart().domainHost();
			Set<String> localParts = collectEdgeCases(emails.edgeCases())
											 .stream()
											 .map(EmailsTests::getLocalPartOfEmail)
											 .collect(Collectors.toSet());

			assertThat(localParts).containsExactlyInAnyOrder("A", "a", "0", "!");
		}

		@Example
		void quotedLocalPart() {
			EmailArbitrary emails = Emails.emails().quotedLocalPart().domainHost();
			Set<String> localParts = collectEdgeCases(emails.edgeCases())
											 .stream()
											 .map(EmailsTests::getLocalPartOfEmail)
											 .collect(Collectors.toSet());

			assertThat(localParts).containsExactlyInAnyOrder("\"A\"", "\"a\"", "\" \"");
		}

		@Example
		void domainHost() {
			EmailArbitrary emails = Emails.emails().unquotedLocalPart().domainHost();
			Set<String> hosts = collectEdgeCases(emails.edgeCases())
											 .stream()
											 .map(EmailsTests::getEmailHost)
											 .collect(Collectors.toSet());

			assertThat(hosts).containsExactlyInAnyOrder(
					"a.aa", "a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.aa"
			);
		}

		@Example
		void ipv4Host() {
			EmailArbitrary emails = Emails.emails().unquotedLocalPart().ipv4Host();
			Set<String> hosts = collectEdgeCases(emails.edgeCases())
											 .stream()
											 .map(EmailsTests::getEmailHost)
											 .collect(Collectors.toSet());

			assertThat(hosts).containsExactlyInAnyOrder(
					"[0.0.0.0]", "[255.255.255.255]", "[127.0.0.1]"
			);
		}

		@Example
		void ipv6Host() {
			EmailArbitrary emails = Emails.emails().unquotedLocalPart().ipv6Host();
			Set<String> hosts = collectEdgeCases(emails.edgeCases())
											 .stream()
											 .map(EmailsTests::getEmailHost)
											 .collect(Collectors.toSet());

			assertThat(hosts).containsExactlyInAnyOrder(
					"[::]",
					"[0:0:0:0:0:0:0:0]",
					"[FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF]", "[ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff]"
			);
		}

	}

	@Provide
	private EmailArbitrary emails() {
		return Emails.emails();
	}

	private boolean isValidIPAddress(String address) {
		if (address.contains(":")) {
			return isValidIPv6Address(address);
		} else {
			return isValidIPv4Address(address);
		}
	}

	private boolean isValidIPv6Address(String address) {
		return DefaultEmailArbitrary.validUseOfColonInIPv6Address(address) && validCharsInIPv6Address(address);
	}

	private boolean validCharsInIPv6Address(String address) {
		String[] addressParts = address.split("\\:");
		if (addressParts.length > 8 || (addressParts.length > 6 && address.endsWith("::"))) {
			return false;
		}
		for (String part : addressParts) {
			if (part.length() > 4) {
				return false;
			}
			for (char c : part.toCharArray()) {
				if (!ALLOWED_CHARS_IPV6_ADDRESS.contains(Character.toString(c))) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isValidIPv4Address(String address) {
		String[] addressParts = address.split("\\.");
		if (addressParts.length != 4) {
			return false;
		}
		for (String part : addressParts) {
			if (part == null || part.length() == 0) {
				return false;
			}
			int partInt = Integer.parseInt(part);
			if (partInt < 0 || partInt > 255) {
				return false;
			}
		}
		return true;
	}

	public static boolean isIPAddress(String domain) {
		if (domain.charAt(0) == '[' && domain.charAt(domain.length() - 1) == ']') {
			return true;
		}
		return false;
	}

	public static boolean isQuoted(String localPart) {
		if (localPart.length() >= 3 && localPart.charAt(0) == '"' && localPart.charAt(localPart.length() - 1) == '"') {
			return true;
		}
		return false;
	}

	public static String getLocalPartOfEmail(String email) {
		int index = email.lastIndexOf('@');
		if (index == -1) {
			index = 0;
		}
		return email.substring(0, index);
	}

	public static String getEmailHost(String email) {
		int index = email.lastIndexOf('@');
		return email.substring(index + 1);
	}

}
