package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.RandomGenerator;

public class DefaultCharacterArbitrary extends NullableArbitrary<Character> implements CharacterArbitrary {

	private Set<Character> allowedChars = new HashSet<>();

	public DefaultCharacterArbitrary() {
		this(new char[0]);
	}

	public DefaultCharacterArbitrary(char[] characters) {
		super(Character.class);
		addAllowedChars(characters);
	}

	public DefaultCharacterArbitrary(char from, char to) {
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

	private void configure(Chars chars) {
		addAllowedChars(chars.value());
		addAllowedChars(chars.from(), chars.to());
	}

	@Override
	public CharacterArbitrary withChars(char[] allowedChars) {
		//TODO: Clone instead of modify
		addAllowedChars(allowedChars);
		return this;
	}

	@Override
	public DefaultCharacterArbitrary withChars(char from, char to) {
		//TODO: Clone instead of modify
		addAllowedChars(from, to);
		return this;
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
