package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.api.constraints.*;
import net.jqwik.properties.RandomGenerator;

public class CharacterArbitrary extends NullableArbitrary<Character> {

	private Set<Character> allowedChars = new HashSet<>();

	public CharacterArbitrary() {
		this(new char[0]);
	}

	public CharacterArbitrary(char[] characters) {
		super(Character.class);
		addAllowedChars(characters);
	}

	public CharacterArbitrary(char from, char to) {
		super(Character.class);
		addAllowedChars(from, to);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected RandomGenerator<Character> baseGenerator(int tries) {
		if (allowedChars.isEmpty()) {
			return RandomGenerators.choose(Character.MIN_VALUE, Character.MAX_VALUE);
		}
		return RandomGenerators.choose(charsArray());
	}

	private Character[] charsArray() {
		return allowedChars.toArray(new Character[allowedChars.size()]);
	}

	public void configure(Chars chars) {
		addAllowedChars(chars.value());
		addAllowedChars(chars.from(), chars.to());
	}

	private void addAllowedChars(char from, char to) {
		if (to > from) {
			for (char c = from; c <= to; c++) {
				allowedChars.add(c);
			}
		}
	}

	private void addAllowedChars(char[] chars) {
		for (char c : chars) {
			allowedChars.add(c);
		}
	}

	public void configure(CharsList charsList) {
		for (Chars chars : charsList.value()) {
			configure(chars);
		}
	}

}
