package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import java.math.*;

public class DefaultCharacterArbitrary extends NullableArbitraryBase<Character> implements CharacterArbitrary {

	public static final int MAX_ASCII_CODEPOINT = 0x007F;

	private final IntegralGeneratingArbitrary generatingArbitrary;

	public DefaultCharacterArbitrary() {
		super(Character.class);
		this.generatingArbitrary = new IntegralGeneratingArbitrary(BigInteger.ZERO, BigInteger.ZERO);
	}

	@Override
	protected RandomGenerator<Character> baseGenerator(int tries) {
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
