package net.jqwik.api;

import com.tngtech.archunit.thirdparty.com.google.common.net.*;

import net.jqwik.api.statistics.*;

import org.assertj.core.api.*;

class EMailsTests {

	@Property(tries = 10)
	void containsAtSign(@ForAll("emails") String email){
		Assertions.assertThat(email).contains("@");
	}

	@Property
	void validLengthBeforeAt(@ForAll("emails") String email){
		String firstPart = getFirstPartOfEmail(email);
		Assertions.assertThat(firstPart.length()).isBetween(1, 64);
	}

	@Property
	void validLengthAfterAt(@ForAll("emails") String email){
		String secondPart = getSecondPartOfEmail(email);
		Assertions.assertThat(secondPart.length()).isBetween(1, 253);
	}

	@Property
	void validSignsBeforeAt(@ForAll("emails") String email){
		String firstPart = getFirstPartOfEmail(email);
		if(isQuoted(firstPart)){
			firstPart = firstPart.substring(1, firstPart.length() - 1);
			firstPart = firstPart.replace( " ", "");
			firstPart = firstPart.replace( "(", "");
			firstPart = firstPart.replace( ")", "");
			firstPart = firstPart.replace( ",", "");
			firstPart = firstPart.replace( ":", "");
			firstPart = firstPart.replace( ";", "");
			firstPart = firstPart.replace( "<", "");
			firstPart = firstPart.replace( ">", "");
			firstPart = firstPart.replace( "@", "");
			firstPart = firstPart.replace( "[", "");
			firstPart = firstPart.replace( "\\\\", "");
			firstPart = firstPart.replace( "]", "");
			firstPart = firstPart.replace( "\\\"", "");
		}
		for(char c = 'a'; c <= 'z'; c++){
			firstPart = firstPart.replace(c + "", "");
		}
		for(char c = 'A'; c <= 'Z'; c++){
			firstPart = firstPart.replace(c + "", "");
		}
		for(char c = '0'; c <= '9'; c++){
			firstPart = firstPart.replace(c + "", "");
		}
		firstPart = firstPart.replace( "!", "");
		firstPart = firstPart.replace( "#", "");
		firstPart = firstPart.replace( "$", "");
		firstPart = firstPart.replace( "%", "");
		firstPart = firstPart.replace( "&", "");
		firstPart = firstPart.replace( "'", "");
		firstPart = firstPart.replace( "*", "");
		firstPart = firstPart.replace( "+", "");
		firstPart = firstPart.replace( "-", "");
		firstPart = firstPart.replace( "/", "");
		firstPart = firstPart.replace( "=", "");
		firstPart = firstPart.replace( "?", "");
		firstPart = firstPart.replace( "^", "");
		firstPart = firstPart.replace( "_", "");
		firstPart = firstPart.replace( "`", "");
		firstPart = firstPart.replace( "{", "");
		firstPart = firstPart.replace( "|", "");
		firstPart = firstPart.replace( "}", "");
		firstPart = firstPart.replace( "~", "");
		firstPart = firstPart.replace( ".", "");
		Assertions.assertThat(firstPart).isEqualTo("");
	}

	@Property
	void validUseOfDotBeforeAt(@ForAll("emails") String email){
		String firstPart = getFirstPartOfEmail(email);
		if(isQuoted(firstPart)){
			return;
		}
		Assertions.assertThat(firstPart).doesNotContain("..");
		Assertions.assertThat(firstPart.charAt(0)).isNotEqualTo('.');
		Assertions.assertThat(firstPart.charAt(firstPart.length() - 1)).isNotEqualTo('.');
	}

	@Property
	void validSignsAfterAt(@ForAll("emails") String email){
		String secondPart = getSecondPartOfEmail(email);
		if(isIPAddress(secondPart)){
			return;
		}
		for(char c = 'a'; c <= 'z'; c++){
			secondPart = secondPart.replace(c + "", "");
		}
		for(char c = 'A'; c <= 'Z'; c++){
			secondPart = secondPart.replace(c + "", "");
		}
		for(char c = '0'; c <= '9'; c++){
			secondPart = secondPart.replace(c + "", "");
		}
		secondPart = secondPart.replace("-", "");
		secondPart = secondPart.replace(".", "");
		Assertions.assertThat(secondPart).isEqualTo("");
	}

	@Property
	void validUseOfHyphenAndDotAfterAt(@ForAll("emails") String email){
		String secondPart = getSecondPartOfEmail(email);
		if(isIPAddress(secondPart)){
			return;
		}
		Assertions.assertThat(secondPart.charAt(0)).isNotEqualTo('-');
		Assertions.assertThat(secondPart.charAt(secondPart.length() - 1)).isNotEqualTo('-');
		Assertions.assertThat(secondPart).doesNotContain("..");
		Assertions.assertThat(secondPart.charAt(0)).isNotEqualTo('.');
		Assertions.assertThat(secondPart.charAt(secondPart.length() - 1)).isNotEqualTo('.');
		if(secondPart.length() >= 2){
			Assertions.assertThat(secondPart.charAt(secondPart.length() - 2)).isNotEqualTo('.');
		}
	}

	@Property
	void validDomainPartLengthAfterAt(@ForAll("emails") String email){
		String secondPart = getSecondPartOfEmail(email);
		if(isIPAddress(secondPart)){
			return;
		}
		while (true) {
			if (secondPart.contains(".")) {
				int startVal = 0;
				int checkVal = secondPart.indexOf('.');
				Assertions.assertThat(checkVal - startVal).isLessThanOrEqualTo(63);
				secondPart = secondPart.substring(checkVal + 1);
			} else {
				Assertions.assertThat(secondPart.length()).isLessThanOrEqualTo(63);
				return;
			}
		}
	}

	@Property
	void validIPAddressAfterAt(@ForAll("emails") String email){
		String secondPart = getSecondPartOfEmail(email);
		if(!isIPAddress(secondPart)){
			return;
		}
		secondPart = secondPart.substring(1, secondPart.length() - 1);
		Assertions.assertThat(InetAddresses.isInetAddress(secondPart)).isTrue();
	}

	@Property
	void checkFirstPartStatistic(@ForAll("emails") String email){
		String firstPart = getFirstPartOfEmail(email);
		Statistics.collect(isQuoted(firstPart));
		Statistics.coverage(statisticsCoverage -> statisticsCoverage.check(true).count(c -> true));
		Statistics.coverage(statisticsCoverage -> statisticsCoverage.check(false).count(c -> false));
	}

	@Property
	void checkSecondPartStatistic(@ForAll("emails") String email){
		String secondPart = getSecondPartOfEmail(email);
		Statistics.collect(isIPAddress(secondPart));
		Statistics.coverage(statisticsCoverage -> statisticsCoverage.check(true).count(c -> true));
		Statistics.coverage(statisticsCoverage -> statisticsCoverage.check(false).count(c -> false));
	}

	private boolean isIPAddress(String secondPart){
		if(secondPart.charAt(0) == '[' && secondPart.charAt(secondPart.length() - 1) == ']'){
			return true;
		}
		return false;
	}

	private boolean isQuoted(String firstPart){
		if(firstPart.length() >= 3 && firstPart.charAt(0) == '"' && firstPart.charAt(firstPart.length() - 1) == '"'){
			return true;
		}
		return false;
	}

	private String getFirstPartOfEmail(String email){
		int index = email.indexOf('@');
		if(index == -1){
			index = 0;
		}
		return email.substring(0, index);
	}

	private String getSecondPartOfEmail(String email){
		int index = email.indexOf('@');
		String substring = email.substring(index + 1);
		return substring;
	}

	@Provide
	Arbitrary<String> emails(){
		return Arbitraries.emails();
	}

}