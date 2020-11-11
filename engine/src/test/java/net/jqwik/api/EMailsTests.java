package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.statistics.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

@PropertyDefaults(edgeCases = EdgeCasesMode.NONE)
@Group
class EMailsTests {

	private static final String ALLOWED_CHARS_DOMAIN = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-.";
	private static final String ALLOWED_CHARS_LOCALPART_UNQUOTED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.!#$%&'*+-/=?^_`{|}~";
	private static final String ALLOWED_CHARS_LOCALPART_QUOTED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.!#$%&'*+-/=?^_`{|}~\"(),:;<>@[\\] ";
	private static final String ALLOWED_CHARS_IPV6_ADDRESS = "0123456789abcdefABCDEF";

	@Group
	class AllGeneratedEmailAddressesAreValid {

		@Property(tries = 10)
		void containsAtSign(@ForAll("emails") String email) {
			assertThat(email).contains("@");
		}

		@Property
		void validLengthBeforeAt(@ForAll("emails") String email) {
			String localPart = getLocalPartOfEmail(email);
			assertThat(localPart.length()).isBetween(1, 64);
		}

		@Property
		void validLengthAfterAt(@ForAll("emails") String email) {
			String domain = getDomainOfEmail(email);
			assertThat(domain.length()).isBetween(1, 253);
		}

		@Property
		void validSignsBeforeAtUnquoted(@ForAll("emails") String email) {
			String localPart = getLocalPartOfEmail(email);
			Assume.that(!isQuoted(localPart));
			assertThat(localPart.chars()).allMatch(c -> stringContainsChar(ALLOWED_CHARS_LOCALPART_UNQUOTED, c));
		}

		@Property
		void validSignsBeforeAtQuoted(@ForAll("emails") String email) {
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
		void validSignsAfterAt(@ForAll("emails") String email) {
			String domain = getDomainOfEmail(email);
			Assume.that(!isIPAddress(domain));
			assertThat(domain.chars()).allMatch(c -> stringContainsChar(ALLOWED_CHARS_DOMAIN, c));
		}

		@Property
		void validUseOfHyphenAndDotAfterAt(@ForAll("emails") String email) {
			String domain = getDomainOfEmail(email);
			Assume.that(!isIPAddress(domain));
			assertThat(domain.charAt(0)).isNotEqualTo('-');
			assertThat(domain.charAt(domain.length() - 1)).isNotEqualTo('-');
			assertThat(domain).doesNotContain("..");
			assertThat(domain.charAt(0)).isNotEqualTo('.');
			assertThat(domain.charAt(domain.length() - 1)).isNotEqualTo('.');
			Assume.that(domain.length() >= 2);
			assertThat(domain.charAt(domain.length() - 2)).isNotEqualTo('.');
		}

		@Property
		void validMaxDomainLengthAfterAt(@ForAll("emails") String email) {
			String domain = getDomainOfEmail(email);
			Assume.that(!isIPAddress(domain));
			String[] domainParts = domain.split("\\.");
			IntStream.range(0, domainParts.length).forEach(i -> {
				assertThat(domainParts[i].length()).isLessThanOrEqualTo(63);
			});
		}

		@Property
		void validIPAddressAfterAt(@ForAll("emails") String email) {
			String domain = getDomainOfEmail(email);
			Assume.that(isIPAddress(domain));
			domain = domain.substring(1, domain.length() - 1);
			assertThat(isValidIPAddress(domain)).isTrue();
		}

		private boolean stringContainsChar(String string, int c) {
			return string.contains(Character.toString((char) c));
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
						  coverage.check(true).percentage(p -> p > 35);
						  coverage.check(false).percentage(p -> p > 35);
					  });
		}

		@Property
		void domainsAndIPAddressesAreGenerated(@ForAll("emails") String email) {
			String domain = getDomainOfEmail(email);
			Statistics.label("Domains")
					  .collect(isIPAddress(domain))
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p > 35);
						  coverage.check(false).percentage(p -> p > 35);
					  });
		}

		@Property
		void IPv4AndIPv6AreGenerated(@ForAll("emails") String email) {
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

		@Property
		void domainsWithOneAndMorePartsAreGenerated(@ForAll("emails") String email) {
			String domain = getDomainOfEmail(email);
			Assume.that(!isIPAddress(domain));
			int domainParts = (int) (domain.chars().filter(v -> v == '.').count() + 1);
			Statistics.label("Domain parts")
					  .collect(domainParts)
					  .coverage(coverage -> {
						  coverage.check(1).count(c -> c >= 1);
						  coverage.check(2).count(c -> c >= 1);
						  coverage.check(3).count(c -> c >= 1);
						  coverage.check(4).count(c -> c >= 1);
						  coverage.check(5).count(c -> c >= 1);
					  });
		}

	}

	@Group
	class ShrinkingTests {

		@Property(tries = 20)
		void defaultShrinking(@ForAll Random random) {
			Arbitrary<String> emails = Arbitraries.emails();
			assertAllValuesAreShrunkTo("A@a", emails, random);
		}

		@Property(tries = 20)
		void domainShrinking(@ForAll Random random) {
			Arbitrary<String> emails = Arbitraries.emails();
			Falsifier<String> falsifier = falsifyDomain();
			String value = shrinkToMinimal(emails, random, falsifier);
			assertThat(value).isEqualTo("A@a");
		}

		@Property(tries = 20)
		void domainShrinkingWithTLD(@ForAll Random random) {
			Arbitrary<String> emails = Arbitraries.emails();
			Falsifier<String> falsifier = falsifyDomainWithTLD();
			String value = shrinkToMinimal(emails, random, falsifier);
			String domain = getDomainOfEmail(value);
			String[] domainParts = domain.split("\\.");
			IntStream.range(0, domainParts.length - 1).forEach(i -> {
				assertThat(domainParts[i]).isEqualTo("a");
			});
			assertThat(domainParts[domainParts.length - 1]).isEqualTo("aa");
		}

		@Property(tries = 50)
		void ipv4Shrinking(@ForAll Random random) {
			Arbitrary<String> emails = Arbitraries.emails();
			Falsifier<String> falsifier = falsifyIPv4();
			String value = shrinkToMinimal(emails, random, falsifier);
			assertThat(value).isEqualTo("A@[0.0.0.0]");
		}

		@Property(tries = 20)
		void ipv6Shrinking(@ForAll Random random) {
			Arbitrary<String> emails = Arbitraries.emails();
			Falsifier<String> falsifier = falsifyIPv6();
			String value = shrinkToMinimal(emails, random, falsifier);
			String domain = getDomainOfEmail(value);
			domain = domain.substring(1, domain.length() - 1);
			String[] domainParts = domain.split(":");
			assertThat(isValidIPv6Address(domain)).isTrue();
			IntStream.range(0, domainParts.length).forEach(i -> {
				assertThat(domainParts[i]).isIn("0", "");
			});
		}

		private TestingFalsifier<String> falsifyDomain() {
			return email -> {
				String domain = getDomainOfEmail(email);
				return isIPAddress(domain);
			};
		}

		private TestingFalsifier<String> falsifyDomainWithTLD() {
			return email -> {
				String domain = getDomainOfEmail(email);
				return !(!isIPAddress(domain) && domain.contains("."));
			};
		}

		private TestingFalsifier<String> falsifyIPv4() {
			return email -> {
				String domain = getDomainOfEmail(email);
				return !(isIPAddress(domain) && domain.contains("."));
			};
		}

		private TestingFalsifier<String> falsifyIPv6() {
			return email -> {
				String domain = getDomainOfEmail(email);
				return !(isIPAddress(domain) && domain.contains(":"));
			};
		}

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
		if ((addressParts.length != 8 && addressParts.length != 6) || (addressParts.length == 6 && !address.endsWith("::"))) {
			return false;
		}
		for (String part : addressParts) {
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

	private boolean isIPAddress(String domain) {
		if (domain.charAt(0) == '[' && domain.charAt(domain.length() - 1) == ']') {
			return true;
		}
		return false;
	}

	private boolean isQuoted(String localPart) {
		if (localPart.length() >= 3 && localPart.charAt(0) == '"' && localPart.charAt(localPart.length() - 1) == '"') {
			return true;
		}
		return false;
	}

	private String getLocalPartOfEmail(String email) {
		int index = email.lastIndexOf('@');
		if (index == -1) {
			index = 0;
		}
		return email.substring(0, index);
	}

	private String getDomainOfEmail(String email) {
		int index = email.lastIndexOf('@');
		String substring = email.substring(index + 1);
		return substring;
	}

	@Provide
	Arbitrary<String> emails() {
		return Arbitraries.emails();
	}

}
