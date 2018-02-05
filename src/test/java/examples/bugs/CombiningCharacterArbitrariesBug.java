package examples.bugs;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class CombiningCharacterArbitrariesBug {

	@Property
	void generateTwoChars(
		@ForAll @Chars(from = 'a', to= 'z') char c1,
		@ForAll @Chars(from = 'a', to= 'z') char c2
	) {
		Character[] characters = {c1, c2};
	}

	@Property(reporting = Reporting.GENERATED)
	void nonMatchingArrayTypeFromProvider(@ForAll("combined") char[] chars) {

	}

	@Provide
	Arbitrary<Character[]> combined() {
		Arbitrary<Character> char1 = Arbitraries.chars('a', 'z');
		Arbitrary<Character> char2 = Arbitraries.chars('0', '9');
		return Combinators.combine(char1, char2).as( (c1, c2) ->
			new Character[] {c1, c2}
		);
	}
}
