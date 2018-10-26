package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.properties.arbitraries.randomized.*;

public class DefaultStringArbitrary extends AbstractArbitraryBase implements StringArbitrary {

	private List<Arbitrary<Character>> characterArbitraries = new ArrayList<>();

	private int minLength = 0;
	private int maxLength = RandomGenerators.DEFAULT_COLLECTION_SIZE;

	private Arbitrary<Character> defaultCharacterArbitrary() {
		return Arbitraries.chars().all().filter(c -> !DefaultStringArbitrary.isNoncharacter(c)
			&& !DefaultStringArbitrary.isPrivateUseCharacter(c));
	}

	public static boolean isNoncharacter(int codepoint) {
		if (codepoint >= 0xfdd0 && codepoint <= 0xfdef)
			return true;
		return codepoint == 0xfffe || codepoint == 0xffff;
	}

	public static boolean isPrivateUseCharacter(int codepoint) {
		return codepoint >= 0xe000 && codepoint <= 0xf8ff;
	}

	@Override
	public RandomGenerator<String> generator(int genSize) {
		final int cutoffLength = RandomGenerators.defaultCutoffSize(minLength, maxLength, genSize);
		List<Shrinkable<String>> samples = Arrays.stream(new String[] { "" })
												 .filter(s -> s.length() >= minLength && s.length() <= maxLength).map(Shrinkable::unshrinkable)
												 .collect(Collectors.toList());
		return RandomGenerators.strings(createCharacterGenerator(), minLength, maxLength, cutoffLength).withEdgeCases(genSize, samples);
	}

	@Override
	public StringArbitrary ofMinLength(int minLength) {
		DefaultStringArbitrary clone = typedClone();
		clone.minLength = minLength;
		return clone;
	}

	@Override
	public StringArbitrary ofMaxLength(int maxLength) {
		DefaultStringArbitrary clone = typedClone();
		clone.maxLength = maxLength;
		return clone;
	}

	@Override
	public StringArbitrary withChars(char... chars) {
		DefaultStringArbitrary clone = typedClone();
		clone.addChars(chars);
		return clone;
	}

	@Override
	public StringArbitrary withCharRange(char from, char to) {
		if (from == 0 && to == 0) {
			return this;
		}
		DefaultStringArbitrary clone = typedClone();
		clone.addCharRange(from, to);
		return clone;
	}

	@Override
	public StringArbitrary ascii() {
		DefaultStringArbitrary clone = typedClone();
		clone.characterArbitraries.add(Arbitraries.chars().ascii());
		return clone;
	}

	@Override
	public StringArbitrary alpha() {
		DefaultStringArbitrary clone = typedClone();
		clone.addCharRange('a', 'z');
		clone.addCharRange('A', 'Z');
		return clone;
	}

	@Override
	public StringArbitrary numeric() {
		DefaultStringArbitrary clone = typedClone();
		clone.addCharRange('0', '9');
		return clone;
	}

	/**
	 * Extracted unicodes from java 8 with
	 * <pre>
	 * 	for (char c = Character.MIN_VALUE; c &lt; Character.MAX_VALUE; c++) {
	 * 		if (Character.isWhitespace(c)) {
	 * 			System.out.println( "\\u" + Integer.toHexString(c | 0x10000).substring(1) );
	 * 		}
	 * 	}
	 *  </pre>
	 */
	@Override
	public StringArbitrary whitespace() {
		return this.withChars( //
			'\u0009', //
			'\n', //
			'\u000b', //
			'\u000c', //
			'\r', //
			'\u001c', //
			'\u001d', //
			'\u001e', //
			'\u001f', //
			'\u0020', //
			'\u1680', //
			'\u180e', //
			'\u2000', //
			'\u2001', //
			'\u2002', //
			'\u2003', //
			'\u2004', //
			'\u2005', //
			'\u2006', //
			'\u2008', //
			'\u2009', //
			'\u200a', //
			'\u2028', //
			'\u2029', //
			'\u205f', //
			'\u3000' //
		);
	}

	@Override
	public StringArbitrary all() {
		return this.withCharRange(Character.MIN_VALUE, Character.MAX_VALUE);
	}

	private void addCharRange(char from, char to) {
		characterArbitraries.add(Arbitraries.chars().between(from, to));
	}

	private void addChars(char[] chars) {
		characterArbitraries.add(Arbitraries.of(chars));
	}

	private RandomGenerator<Character> createCharacterGenerator() {
		if (characterArbitraries.isEmpty()) {
			return defaultCharacterArbitrary().generator(1);
		}
		Arbitrary<Character> first = characterArbitraries.get(0);
		@SuppressWarnings("unchecked")
		Arbitrary<Character>[] rest = characterArbitraries.subList(1, characterArbitraries.size()).toArray(new Arbitrary[characterArbitraries.size() - 1]);

		Arbitrary<Character> allValidCharacters = Arbitraries.oneOf(first, rest);
		return allValidCharacters.generator(1);
	}

}
