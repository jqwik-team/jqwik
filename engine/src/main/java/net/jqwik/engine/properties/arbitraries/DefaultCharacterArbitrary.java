package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultCharacterArbitrary extends TypedCloneable implements CharacterArbitrary {

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

	private List<Tuple.Tuple2<Integer, Arbitrary<Character>>> partsWithSize = new ArrayList<>();

	public DefaultCharacterArbitrary() {
	}

	@Override
	public RandomGenerator<Character> generator(int genSize) {
		return arbitrary().generator(genSize);
	}

	private Arbitrary<Character> arbitrary() {
		if (partsWithSize.isEmpty()) {
			return defaultArbitrary();
		}
		if (partsWithSize.size() == 1) {
			return partsWithSize.get(0).get2();
		}

		return Arbitraries.frequencyOf(partsWithSize);
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
	public EdgeCases<Character> edgeCases(int maxEdgeCases) {
		return arbitrary().edgeCases(maxEdgeCases);
	}

	@Override
	public CharacterArbitrary range(char min, char max) {
		return cloneWith(rangeArbitrary(min, max), max - min + 1);
	}

	@Override
	public CharacterArbitrary with(char... allowedChars) {
		return cloneWith(charsArbitrary(allowedChars), allowedChars.length);
	}

	@Override
	public CharacterArbitrary with(CharSequence allowedChars) {
		char[] chars = allowedChars.toString().toCharArray();
		return with(chars);
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
	public CharacterArbitrary numeric() {
		return range('0', '9');
	}

	@Override
	public CharacterArbitrary whitespace() {
		return with(DefaultCharacterArbitrary.WHITESPACE_CHARS);
	}

	@Override
	public CharacterArbitrary alpha() {
		return this.range('A', 'Z').range('a', 'z');
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultCharacterArbitrary that = (DefaultCharacterArbitrary) o;
		return partsWithSize.equals(that.partsWithSize);
	}

	@Override
	public int hashCode() {
		return partsWithSize.hashCode();
	}

	private CharacterArbitrary cloneWith(Arbitrary<Character> part, int size) {
		DefaultCharacterArbitrary clone = super.typedClone();
		clone.partsWithSize = new ArrayList<>(partsWithSize);
		clone.partsWithSize.add(Tuple.of(size, part));
		return clone;
	}

	private Arbitrary<Character> rangeArbitrary(char min, char max) {
		return new CharacterRangeArbitrary(min, max);
	}

}
