package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultCharacterArbitrary extends AbstractArbitraryBase<Character> implements CharacterArbitrary {

	private Set<Character> allowedChars = new HashSet<>();

	public DefaultCharacterArbitrary() {
		super(Character.class);
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

	@Override
	public CharacterArbitrary withChars(char[] allowedChars) {
		DefaultCharacterArbitrary clone = typedClone();
		clone.addAllowedChars(allowedChars);
		return clone;
	}

	@Override
	public CharacterArbitrary withChars(char from, char to) {
		DefaultCharacterArbitrary clone = typedClone();
		clone.addAllowedChars(from, to);
		return clone;
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

}
