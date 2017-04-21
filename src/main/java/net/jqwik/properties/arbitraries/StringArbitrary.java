package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class StringArbitrary extends NullableArbitrary<String> {

	private static char[] defaultChars =
		{'a', 'b', 'y', 'z', 'A', 'B', 'Y', 'Z', '0', '9', ' ', ',', '.', '!', '@'};

	private RandomGenerator<Character> characterGenerator;
	private int maxSize;

	public StringArbitrary() {
		this(defaultGenerator(), 0);
	}

	private static RandomGenerator<Character> defaultGenerator() {
		return RandomGenerators.choose(defaultChars);
	}

	public StringArbitrary(RandomGenerator<Character> characterGenerator, int maxSize) {
		super(String.class);
		this.characterGenerator = characterGenerator;
		this.maxSize = maxSize;
	}

	public StringArbitrary(char[] characters, int maxSize) {
		this(createGenerator(characters), maxSize);
	}

	public StringArbitrary(char[] characters) {
		this(characters, 0);
	}

	private static RandomGenerator<Character> createGenerator(char[] characters) {
		return RandomGenerators.choose(characters);
	}

	public StringArbitrary(char from, char to, int maxLength) {
		this(createGenerator(from, to), maxLength);
	}

	public StringArbitrary(char from, char to) {
		this(from, to, 0);
	}

	private static RandomGenerator<Character> createGenerator(char from, char to) {
		return RandomGenerators.choose(from, to);
	}

	@Override
	protected RandomGenerator<String> baseGenerator(int tries) {
		int effectiveMaxSize = maxSize;
		if (effectiveMaxSize <= 0)
			effectiveMaxSize = Arbitrary.defaultMaxFromTries(tries);
		return RandomGenerators.string(characterGenerator, effectiveMaxSize);
	}

	public void configure(MaxSize maxSize) {
		this.maxSize = maxSize.value();
	}


}
