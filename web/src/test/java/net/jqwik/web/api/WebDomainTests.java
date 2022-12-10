package net.jqwik.web.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;
import static net.jqwik.web.api.WebTestingSupport.*;

@Group
class WebDomainTests {

	@Provide
	Arbitrary<String> webDomains() {
		return Web.webDomains();
	}

	@Property
	boolean webDomainAnnotationIsHonoured(@ForAll @WebDomain String domain) {
		return isValidWebDomain(domain);
	}

	@Property
	@StatisticsReport(onFailureOnly = true)
	void domainHostsWithTwoAndMorePartsAreGenerated(@ForAll("webDomains") String domain) {
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

	@Example
	void noExhaustiveGeneration() {
		assertThat(webDomains().exhaustive()).isNotPresent();
	}

	@Example
	void edgeCases() {
		Set<String> edgeCaseValues = collectEdgeCaseValues(webDomains().edgeCases());
		assertThat(edgeCaseValues).containsExactlyInAnyOrder("a.aa", "0.aa");
	}

	@Group
	class ShrinkingTests {

		@Property(tries = 10)
		@Label("shrink down to a.aa")
		void shrinkDownToAdotAA(@ForAll JqwikRandom random) {
			Arbitrary<String> domains = Web.webDomains();
			String value = falsifyThenShrink(domains.generator(1000), random, TestingFalsifier.alwaysFalsify());
			assertThat(value).isEqualTo("a.aa");
		}

		@Property(tries = 10)
		void shrinkTo4Subdomains(@ForAll JqwikRandom random) {
			Arbitrary<String> domains = Web.webDomains();
			Falsifier<String> falsifier = domain -> {
				long countDots = domain.chars().filter(c -> c == '.').count();
				if (countDots >= 3) {
					return TryExecutionResult.falsified(null);
				}
				return TryExecutionResult.satisfied();
			};
			String value = falsifyThenShrink(domains.generator(1000), random, falsifier);
			assertThat(value).isEqualTo("a.a.a.aa");
		}
	}

	@Group
	class ValidWebDomains {

		@Property
		boolean allDomainsAreValid(@ForAll("webDomains") String domain) {
			return isValidWebDomain(domain);
		}

		@Property
		void validUseOfHyphenAndDotAfterAt(@ForAll("webDomains") String domain) {
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
		void validMaxDomainLengthAfterAt(@ForAll("webDomains") String domain) {
			String[] domainParts = domain.split("\\.");
			IntStream.range(0, domainParts.length).forEach(i -> {
				assertThat(domainParts[i].length()).isLessThanOrEqualTo(63);
			});
		}

		@Property(edgeCases = EdgeCasesMode.NONE)
		void validCharsAfterAt(@ForAll("webDomains") String domain) {
			assertThat(domain.chars()).allMatch(c -> isIn(c, ALLOWED_CHARS_DOMAIN));
		}

		@Property
		void tldMustNotStartWithNumber(@ForAll("webDomains") String domain) {
			String[] domainParts = domain.split("\\.");
			Assume.that(domainParts.length >= 2);
			String tld = domainParts[domainParts.length - 1];
			assertThat(doesNotStartWithDigit(tld)).isTrue();
		}
	}

}
