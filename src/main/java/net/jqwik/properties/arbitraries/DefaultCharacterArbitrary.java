package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import java.math.*;

public class DefaultCharacterArbitrary extends AbstractArbitraryBase implements CharacterArbitrary {

	public static final int MAX_ASCII_CODEPOINT = 0x007F;

	private final IntegralGeneratingArbitrary generatingArbitrary;

	public DefaultCharacterArbitrary() {
		this.generatingArbitrary = new IntegralGeneratingArbitrary(BigInteger.ZERO, BigInteger.ZERO);
	}

	@Override
	public RandomGenerator<Character> generator(int tries) {
		return this.generatingArbitrary.generator(tries).map(bigInteger -> (char) bigInteger.intValueExact());
	}

	@Override
	public CharacterArbitrary between(char min, char max) {
		DefaultCharacterArbitrary clone = typedClone();
		clone.generatingArbitrary.min = BigInteger.valueOf(min);
		clone.generatingArbitrary.max = BigInteger.valueOf(max);
		return clone;
	}

	@Override
	public CharacterArbitrary ascii() {
		return between((char) Character.MIN_CODE_POINT, (char) MAX_ASCII_CODEPOINT);
	}

	@Override
	public CharacterArbitrary all() {
		return between(Character.MIN_VALUE, Character.MAX_VALUE);
	}

	@Override
	public CharacterArbitrary digit() {
		return between('0', '9');
	}

}
