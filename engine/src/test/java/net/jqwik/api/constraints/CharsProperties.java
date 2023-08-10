package net.jqwik.api.constraints;

import net.jqwik.api.*;

@Group
class CharsProperties {

	@Property
	boolean stringWithCharRange(@ForAll @CharRange(from = 'a', to = 'z') String aString) {
		return aString.chars().allMatch(aChar -> aChar >= 'a' && aChar <= 'z');
	}

	@Property
	boolean stringWithSeveralCharRanges(@ForAll @CharRange(from = 'a', to = 'z') @CharRange(from = '1', to = '9') String aString) {
		return aString.chars().allMatch(
			aChar -> (aChar >= 'a' && aChar <= 'z')
						 || (aChar >= '1' && aChar <= '9')
		);
	}

	@Property
	boolean charsWithRange(@ForAll @CharRange(from = 'a', to = 'z') char value) {
		return value >= 'a' && value <= 'z';
	}

	@Property
	boolean charsWithSeveralRanges(@ForAll @CharRange(from = 'a', to = 'z') @CharRange(from = '1', to = '9') char value) {
		return (value >= 'a' && value <= 'z') || (value >= '1' && value <= '9');
	}

	@Property
	boolean charArray(@ForAll @Chars({'1', '2', '3'}) String aString) {
		return aString.chars().allMatch(aChar -> aChar >= '1' && aChar <= '3');
	}

	@Property
	boolean listOfChars(@ForAll @Chars({'1', '2', '3'}) @Chars({'a', 'b', 'c'}) String aString) {
		return aString.chars().allMatch(
			aChar -> (aChar >= '1' && aChar <= '3')
						 || (aChar >= 'a' && aChar <= 'c')
		);
	}
}
