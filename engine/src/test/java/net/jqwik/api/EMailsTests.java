package net.jqwik.api;

import com.tngtech.archunit.thirdparty.com.google.common.net.*;

import net.jqwik.api.statistics.*;

import org.assertj.core.api.*;

import java.util.stream.*;

class EMailsTests {

	@Property(tries = 10, edgeCases = EdgeCasesMode.NONE)
	void containsAtSign(@ForAll("emails") String email){
		Assertions.assertThat(email).contains("@");
	}

	@Property(edgeCases = EdgeCasesMode.NONE)
	void validLengthBeforeAt(@ForAll("emails") String email){
		String localPart = getLocalPartOfEmail(email);
		Assertions.assertThat(localPart.length()).isBetween(1, 64);
	}

	@Property(edgeCases = EdgeCasesMode.NONE)
	void validLengthAfterAt(@ForAll("emails") String email){
		String domain = getDomainOfEmail(email);
		Assertions.assertThat(domain.length()).isBetween(1, 253);
	}

	@Property(edgeCases = EdgeCasesMode.NONE)
	void validSignsBeforeAt(@ForAll("emails") String email){
		String localPart = getLocalPartOfEmail(email);
		if(isQuoted(localPart)){
			localPart = localPart.substring(1, localPart.length() - 1);
			localPart = localPart.replace( " ", "");
			localPart = localPart.replace( "(", "");
			localPart = localPart.replace( ")", "");
			localPart = localPart.replace( ",", "");
			localPart = localPart.replace( ":", "");
			localPart = localPart.replace( ";", "");
			localPart = localPart.replace( "<", "");
			localPart = localPart.replace( ">", "");
			localPart = localPart.replace( "@", "");
			localPart = localPart.replace( "[", "");
			localPart = localPart.replace( "\\\\", "");
			localPart = localPart.replace( "]", "");
			localPart = localPart.replace( "\\\"", "");
		}
		for(char c = 'a'; c <= 'z'; c++){
			localPart = localPart.replace(c + "", "");
		}
		for(char c = 'A'; c <= 'Z'; c++){
			localPart = localPart.replace(c + "", "");
		}
		for(char c = '0'; c <= '9'; c++){
			localPart = localPart.replace(c + "", "");
		}
		localPart = localPart.replace( "!", "");
		localPart = localPart.replace( "#", "");
		localPart = localPart.replace( "$", "");
		localPart = localPart.replace( "%", "");
		localPart = localPart.replace( "&", "");
		localPart = localPart.replace( "'", "");
		localPart = localPart.replace( "*", "");
		localPart = localPart.replace( "+", "");
		localPart = localPart.replace( "-", "");
		localPart = localPart.replace( "/", "");
		localPart = localPart.replace( "=", "");
		localPart = localPart.replace( "?", "");
		localPart = localPart.replace( "^", "");
		localPart = localPart.replace( "_", "");
		localPart = localPart.replace( "`", "");
		localPart = localPart.replace( "{", "");
		localPart = localPart.replace( "|", "");
		localPart = localPart.replace( "}", "");
		localPart = localPart.replace( "~", "");
		localPart = localPart.replace( ".", "");
		Assertions.assertThat(localPart).isEqualTo("");
	}

	@Property(edgeCases = EdgeCasesMode.NONE)
	void validUseOfDotBeforeAt(@ForAll("emails") String email){
		String localPart = getLocalPartOfEmail(email);
		Assume.that(!isQuoted(localPart));
		Assertions.assertThat(localPart).doesNotContain("..");
		Assertions.assertThat(localPart.charAt(0)).isNotEqualTo('.');
		Assertions.assertThat(localPart.charAt(localPart.length() - 1)).isNotEqualTo('.');
	}

	@Property(edgeCases = EdgeCasesMode.NONE)
	void validSignsAfterAt(@ForAll("emails") String email){
		String domain = getDomainOfEmail(email);
		Assume.that(!isIPAddress(domain));
		for(char c = 'a'; c <= 'z'; c++){
			domain = domain.replace(c + "", "");
		}
		for(char c = 'A'; c <= 'Z'; c++){
			domain = domain.replace(c + "", "");
		}
		for(char c = '0'; c <= '9'; c++){
			domain = domain.replace(c + "", "");
		}
		domain = domain.replace("-", "");
		domain = domain.replace(".", "");
		Assertions.assertThat(domain).isEqualTo("");
	}

	@Property(edgeCases = EdgeCasesMode.NONE)
	void validUseOfHyphenAndDotAfterAt(@ForAll("emails") String email){
		String domain = getDomainOfEmail(email);
		Assume.that(!isIPAddress(domain));
		Assertions.assertThat(domain.charAt(0)).isNotEqualTo('-');
		Assertions.assertThat(domain.charAt(domain.length() - 1)).isNotEqualTo('-');
		Assertions.assertThat(domain).doesNotContain("..");
		Assertions.assertThat(domain.charAt(0)).isNotEqualTo('.');
		Assertions.assertThat(domain.charAt(domain.length() - 1)).isNotEqualTo('.');
		Assume.that(domain.length() >= 2);
		Assertions.assertThat(domain.charAt(domain.length() - 2)).isNotEqualTo('.');
	}

	@Property(edgeCases = EdgeCasesMode.NONE)
	void validMaxDomainLengthAfterAt(@ForAll("emails") String email){
		String domain = getDomainOfEmail(email);
		Assume.that(!isIPAddress(domain));
		String[] domainParts = domain.split("\\.");
		IntStream.range(0, domainParts.length).forEach(i -> {
			Assertions.assertThat(domainParts[i].length()).isLessThanOrEqualTo(63);
		});
	}

	@Property(edgeCases = EdgeCasesMode.NONE)
	void validIPAddressAfterAt(@ForAll("emails") String email){
		String domain = getDomainOfEmail(email);
		Assume.that(isIPAddress(domain));
		domain = domain.substring(1, domain.length() - 1);
		Assertions.assertThat(InetAddresses.isInetAddress(domain)).isTrue();
	}

	@Property(edgeCases = EdgeCasesMode.NONE)
	void quotedAndUnquotedUsernamesAreGenerated(@ForAll("emails") String email){
		String localPart = getLocalPartOfEmail(email);
		Statistics.collect(isQuoted(localPart));
		Statistics.coverage(coverage -> {
			coverage.check(true).count(c -> c >= 1);
			coverage.check(false).count(c -> c >= 1);
		});
	}

	@Property(edgeCases = EdgeCasesMode.NONE)
	void domainsAndIPAddressesAreGenerated(@ForAll("emails") String email){
		String domain = getDomainOfEmail(email);
		Statistics.collect(isIPAddress(domain));
		Statistics.coverage(coverage -> {
			coverage.check(true).count(c -> c >= 1);
			coverage.check(false).count(c -> c >= 1);
		});
	}

	@Property(edgeCases = EdgeCasesMode.NONE)
	void IPv4AndIPv6AreGenerated(@ForAll("emails") String email){
		String domain = getDomainOfEmail(email);
		Assume.that(isIPAddress(domain));
		domain = domain.substring(1, domain.length() - 1);
		Statistics.collect(domain.contains("."));
		Statistics.coverage(coverage -> {
			coverage.check(true).count(c -> c >= 1);
			coverage.check(false).count(c -> c >= 1);
		});
	}

	private boolean isIPAddress(String domain){
		if(domain.charAt(0) == '[' && domain.charAt(domain.length() - 1) == ']'){
			return true;
		}
		return false;
	}

	private boolean isQuoted(String localPart){
		if(localPart.length() >= 3 && localPart.charAt(0) == '"' && localPart.charAt(localPart.length() - 1) == '"'){
			return true;
		}
		return false;
	}

	private String getLocalPartOfEmail(String email){
		int index = email.lastIndexOf('@');
		if(index == -1){
			index = 0;
		}
		return email.substring(0, index);
	}

	private String getDomainOfEmail(String email){
		int index = email.lastIndexOf('@');
		String substring = email.substring(index + 1);
		return substring;
	}

	@Provide
	Arbitrary<String> emails(){
		return Arbitraries.emails();
	}

}
