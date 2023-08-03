package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class ChooseCharacterArbitrary extends UseGeneratorsArbitrary<Character> {

	private final char[] chars;

	public ChooseCharacterArbitrary(char[] chars) {
		super(
			RandomGenerators.choose(chars),
			max -> ExhaustiveGenerators.choose(chars, max),
			maxEdgeCases -> {
				List<Character> validCharacters = new ArrayList<>(chars.length);
				for (char character : chars) {
					validCharacters.add(character);
				}
				return EdgeCasesSupport.choose(validCharacters, maxEdgeCases);
			}
		);
		this.chars = chars;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ChooseCharacterArbitrary that = (ChooseCharacterArbitrary) o;
		return Arrays.equals(chars, that.chars);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(chars);
	}
}
