package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultCharacterArbitrary extends AbstractArbitraryBase implements CharacterArbitrary {

	static final char[] WHITESPACE_CHARS;

	static {
		// determine WHITESPACE_CHARS at runtime because the environments differ . . .
		final StringBuilder whitespace =
			IntStream.range(Character.MIN_VALUE, Character.MAX_VALUE + 1)
					 .filter(Character::isWhitespace)
					 .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append);

		final int whitespaceLength = whitespace.length();
		final char[] charArray = new char[whitespaceLength];
		whitespace.getChars(0, whitespaceLength, charArray, 0);
		WHITESPACE_CHARS = charArray;
	}

	static final int MAX_ASCII_CODEPOINT = 0x007F;

	static boolean isNoncharacter(int codepoint) {
		if (codepoint >= 0xfdd0 && codepoint <= 0xfdef)
			return true;
		// see https://en.wikipedia.org/wiki/UTF-16#U+D800_to_U+DFFF
		if (codepoint >= 0xd800 && codepoint <= 0xdfff)
			return true;
		return codepoint == 0xfffe || codepoint == 0xffff;
	}

	static boolean isPrivateUseCharacter(int codepoint) {
		return codepoint >= 0xe000 && codepoint <= 0xf8ff;
	}

	private List<Arbitrary<Character>> parts = new ArrayList<>();

	public DefaultCharacterArbitrary() {
	}

	@Override
	public RandomGenerator<Character> generator(int genSize) {
		return arbitrary().generator(genSize);
	}

	private Arbitrary<Character> arbitrary() {
		if (parts.isEmpty()) {
			return defaultArbitrary();
		}
		if (parts.size() == 1) {
			return parts.get(0);
		}
		return Arbitraries.oneOf(parts);
	}

	private Arbitrary<Character> defaultArbitrary() {
		return rangeArbitrary(Character.MIN_VALUE, Character.MAX_VALUE)
				   .filter(c -> !DefaultCharacterArbitrary.isNoncharacter(c)
									&& !DefaultCharacterArbitrary.isPrivateUseCharacter(c));
	}

	@Override
	public Optional<ExhaustiveGenerator<Character>> exhaustive(long maxNumberOfSamples) {
		return arbitrary().exhaustive(maxNumberOfSamples);
	}

	@Override
	public EdgeCases<Character> edgeCases() {
		return arbitrary().edgeCases();
	}

	@Override
	public CharacterArbitrary range(char min, char max) {
		return cloneWith(rangeArbitrary(min, max));
	}

	@Override
	public CharacterArbitrary with(char... allowedChars) {
		return cloneWith(charsArbitrary(allowedChars));
	}

	@Override
	public CharacterArbitrary with(CharSequence allowedChars) {
		return cloneWith(charsArbitrary(allowedChars.toString().toCharArray()));
	}

	private Arbitrary<Character> charsArbitrary(char[] allowedChars) {
		return Arbitraries.of(allowedChars);
	}

	@Override
	public CharacterArbitrary all() {
		return new DefaultCharacterArbitrary();
	}

	@Override
	public CharacterArbitrary ascii() {
		return range((char) Character.MIN_CODE_POINT, (char) MAX_ASCII_CODEPOINT);
	}

	@Override
	public CharacterArbitrary digit() {
		return range('0', '9');
	}

	@Override
	public CharacterArbitrary whitespace() {
		return with(DefaultCharacterArbitrary.WHITESPACE_CHARS);
	}

	private CharacterArbitrary cloneWith(Arbitrary<Character> part) {
		DefaultCharacterArbitrary clone = super.typedClone();
		clone.parts = new ArrayList<>(parts);
		clone.parts.add(part);
		return clone;
	}

	private Arbitrary<Character> rangeArbitrary(char min, char max) {
		return new CharacterRangeArbitrary(min, max);
	}

}
