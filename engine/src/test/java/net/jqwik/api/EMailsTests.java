package net.jqwik.api;

import com.tngtech.archunit.thirdparty.com.google.common.net.*;

import static net.jqwik.api.ArbitraryTestHelper.*;

public class EMailsTests {

	@Example
	void containsAtSign(){
		RandomGenerator<String> generator = getEmailGeneratorFromArbitrary();
		assertAllGenerated(generator, (String value) -> value.contains("@"));
	}

	@Example
	void validLengthBeforeAt(){
		RandomGenerator<String> generator = getEmailGeneratorFromArbitrary();
		assertAllGenerated(generator, (String value) -> firstPartLengthCheck(value));
	}

	private boolean firstPartLengthCheck(String email){
		String firstPart = getFirstPartOfEmail(email);
		return firstPart.length() > 0 && firstPart.length() <= 64;
	}

	@Example
	void validLengthAfterAt(){
		RandomGenerator<String> generator = getEmailGeneratorFromArbitrary();
		assertAllGenerated(generator, (String value) -> secondPartLengthCheck(value));
	}

	private boolean secondPartLengthCheck(String email){
		String secondPart = getSecondPartOfEmail(email);
		return secondPart.length() > 0 && secondPart.length() <= 253;
	}

	@Example
	void validSignsAfterAt(){
		RandomGenerator<String> generator = getEmailGeneratorFromArbitrary();
		assertAllGenerated(generator, (String value) -> secondPartSignCheck(value));
	}

	private boolean secondPartSignCheck(String email){
		String secondPart = getSecondPartOfEmail(email);
		if(isIPAddress(secondPart)){
			return true;
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
		return secondPart.equals("");
	}

	@Example
	void validUseOfHyphenAndDotAfterAt(){
		RandomGenerator<String> generator = getEmailGeneratorFromArbitrary();
		assertAllGenerated(generator, (String value) -> checkHypenAndDotUseAfterAt(value));
	}

	private boolean checkHypenAndDotUseAfterAt(String email){
		String secondPart = getSecondPartOfEmail(email);
		if(isIPAddress(secondPart)){
			return true;
		}
		if(secondPart.charAt(0) == '-' || secondPart.charAt(secondPart.length() - 1) == '-'){
			return false;
		}
		if(secondPart.contains("..") || secondPart.charAt(0) == '.' || secondPart.charAt(secondPart.length() - 1) == '.' || (secondPart.length() >= 2 && secondPart.charAt(secondPart.length() - 2) == '.')){
			return false;
		}
		return true;
	}

	@Example
	void validDomainPartLengthAfterAt(){
		RandomGenerator<String> generator = getEmailGeneratorFromArbitrary();
		assertAllGenerated(generator, (String value) -> checkDomainPartLengthAfterAt(value));
	}

	private boolean checkDomainPartLengthAfterAt(String email){
		String secondPart = getSecondPartOfEmail(email);
		if(isIPAddress(secondPart)){
			return true;
		}
		while (true) {
			if (secondPart.contains(".")) {
				int startVal = 0;
				int checkVal = secondPart.indexOf('.');
				if (checkVal - startVal > 63) {
					return false;
				}
				secondPart = secondPart.substring(checkVal + 1);
			} else {
				return secondPart.length() <= 63;
			}
		}
	}

	@Example
	void validIPAddressAfterAt(){
		RandomGenerator<String> generator = getEmailGeneratorFromArbitrary();
		assertAllGenerated(generator, (String value) -> IPAddressCheck(value));
	}

	private boolean IPAddressCheck(String email){
		String secondPart = getSecondPartOfEmail(email);
		if(!isIPAddress(secondPart)){
			return true;
		}
		secondPart = secondPart.substring(1, secondPart.length() - 1);
		System.out.println(secondPart);
		return InetAddresses.isInetAddress(secondPart);
	}

	private boolean isIPAddress(String secondPart){
		if(secondPart.charAt(0) == '[' || secondPart.charAt(secondPart.length() - 1) == ']'){
			return true;
		}
		return false;
	}

	private RandomGenerator<String> getEmailGeneratorFromArbitrary(){
		Arbitrary<String> emailArbitrary = Arbitraries.emails();
		return emailArbitrary.generator(1);
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
		return email.substring(index + 1);
	}

}