package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class NStringArbitrary extends NullableArbitrary<String> {

	private final static char[] defaultChars = {'a', 'b', 'y', 'z', 'A', 'B', 'Y', 'Z', '0', '9', ' ', ',', '.', '!', '@'};

	private RandomGenerator<Character> characterGenerator;
	private int maxSize;

	public NStringArbitrary() {
		this(defaultGenerator(), 0);
	}

	private static RandomGenerator<Character> defaultGenerator() {
		return RandomGenerators.choose(defaultChars);
	}

	public NStringArbitrary(RandomGenerator<Character> characterGenerator, int maxSize) {
		super(String.class);
		this.characterGenerator = characterGenerator;
		this.maxSize = maxSize;
	}

	public NStringArbitrary(char[] characters, int maxSize) {
		this(createGenerator(characters), maxSize);
	}

	public NStringArbitrary(char[] characters) {
		this(characters, 0);
	}

	private static RandomGenerator<Character> createGenerator(char[] characters) {
		return RandomGenerators.choose(characters);
	}

	public NStringArbitrary(char from, char to, int maxLength) {
		this(createGenerator(from, to), maxLength);
	}

	public NStringArbitrary(char from, char to) {
		this(from, to, 0);
	}

	private static RandomGenerator<Character> createGenerator(char from, char to) {
		return RandomGenerators.choose(from, to);
	}

	@Override
	protected RandomGenerator<String> baseGenerator(int tries) {
		int effectiveMaxSize = maxSize;
		if (effectiveMaxSize <= 0) effectiveMaxSize = Arbitrary.defaultMaxFromTries(tries);
		return RandomGenerators.string(characterGenerator, effectiveMaxSize);
	}

	public void configure(MaxStringLength maxStringLength) {
		this.maxSize = maxStringLength.value();
	}

	public void configure(ValidChars validChars) {
		Optional<RandomGenerator<Character>> charsGenerator = createCharsGenerator(validChars);
		Optional<RandomGenerator<Character>> fromToGenerator = createFromToGenerator(validChars);

		double mixInProbability = calculateMixInProbability(validChars);
		Optional<RandomGenerator<Character>> generator = mix(charsGenerator, fromToGenerator, mixInProbability);
		generator.ifPresent(gen -> characterGenerator = gen);
	}

	private double calculateMixInProbability(ValidChars validChars) {
		double sizeChars = validChars.value().length;
		double sizeFromTo = validChars.to() - validChars.from();
		return sizeFromTo != 0.0 ? sizeFromTo / (sizeChars + sizeFromTo) : 1.0;
	}

	private Optional<RandomGenerator<Character>> mix( //
													  Optional<RandomGenerator<Character>> charsGenerator, //
													  Optional<RandomGenerator<Character>> fromToGenerator, //
													  double mixInProbability) {

		if (charsGenerator.isPresent()) {
			return fromToGenerator //
				.map(fromTo -> Optional.of(charsGenerator.get().mixIn(fromTo, mixInProbability))) //
				.orElse(charsGenerator);
		}
		return fromToGenerator;
	}

	private Optional<RandomGenerator<Character>> createFromToGenerator(ValidChars validChars) {
		RandomGenerator<Character> fromToGenerator = null;
		if (validChars.from() > 0 && validChars.to() > 0) {
			fromToGenerator = RandomGenerators.choose(validChars.from(), validChars.to());
			characterGenerator = fromToGenerator;
		}
		return Optional.ofNullable(fromToGenerator);
	}

	private Optional<RandomGenerator<Character>> createCharsGenerator(ValidChars validChars) {
		RandomGenerator<Character> charsGenerator = null;
		if (validChars.value().length > 0) {
			charsGenerator = RandomGenerators.choose(validChars.value());
			characterGenerator = charsGenerator;
		}
		return Optional.ofNullable(charsGenerator);
	}

}
