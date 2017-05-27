package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class NStringArbitrary extends NNullableArbitrary<String> {

	private final static char[] defaultChars = {'a', 'b', 'y', 'z', 'A', 'B', 'Y', 'Z', '0', '9', ' ', ',', '.', '!', '@'};

	private NShrinkableGenerator<Character> characterGenerator;
	private int maxSize;

	public NStringArbitrary() {
		this(defaultGenerator(), 0);
	}

	private static NShrinkableGenerator<Character> defaultGenerator() {
		return NShrinkableGenerators.choose(defaultChars);
	}

	public NStringArbitrary(NShrinkableGenerator<Character> characterGenerator, int maxSize) {
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

	private static NShrinkableGenerator<Character> createGenerator(char[] characters) {
		return NShrinkableGenerators.choose(characters);
	}

	public NStringArbitrary(char from, char to, int maxLength) {
		this(createGenerator(from, to), maxLength);
	}

	public NStringArbitrary(char from, char to) {
		this(from, to, 0);
	}

	private static NShrinkableGenerator<Character> createGenerator(char from, char to) {
		return NShrinkableGenerators.choose(from, to);
	}

	@Override
	protected NShrinkableGenerator<String> baseGenerator(int tries) {
		int effectiveMaxSize = maxSize;
		if (effectiveMaxSize <= 0) effectiveMaxSize = NArbitrary.defaultMaxFromTries(tries);
		return NShrinkableGenerators.string(characterGenerator, effectiveMaxSize);
	}

	public void configure(MaxStringLength maxStringLength) {
		this.maxSize = maxStringLength.value();
	}

	public void configure(ValidChars validChars) {
		Optional<NShrinkableGenerator<Character>> charsGenerator = createCharsGenerator(validChars);
		Optional<NShrinkableGenerator<Character>> fromToGenerator = createFromToGenerator(validChars);

		double mixInProbability = calculateMixInProbability(validChars);
		Optional<NShrinkableGenerator<Character>> generator = mix(charsGenerator, fromToGenerator, mixInProbability);
		generator.ifPresent(gen -> characterGenerator = gen);
	}

	private double calculateMixInProbability(ValidChars validChars) {
		double sizeChars = validChars.value().length;
		double sizeFromTo = validChars.to() - validChars.from();
		return sizeFromTo != 0.0 ? sizeFromTo / (sizeChars + sizeFromTo) : 1.0;
	}

	private Optional<NShrinkableGenerator<Character>> mix( //
													  Optional<NShrinkableGenerator<Character>> charsGenerator, //
													  Optional<NShrinkableGenerator<Character>> fromToGenerator, //
													  double mixInProbability) {

		if (charsGenerator.isPresent()) {
			return fromToGenerator //
				.map(fromTo -> Optional.of(charsGenerator.get().mixIn(fromTo, mixInProbability))) //
				.orElse(charsGenerator);
		}
		return fromToGenerator;
	}

	private Optional<NShrinkableGenerator<Character>> createFromToGenerator(ValidChars validChars) {
		NShrinkableGenerator<Character> fromToGenerator = null;
		if (validChars.from() > 0 && validChars.to() > 0) {
			fromToGenerator = NShrinkableGenerators.choose(validChars.from(), validChars.to());
			characterGenerator = fromToGenerator;
		}
		return Optional.ofNullable(fromToGenerator);
	}

	private Optional<NShrinkableGenerator<Character>> createCharsGenerator(ValidChars validChars) {
		NShrinkableGenerator<Character> charsGenerator = null;
		if (validChars.value().length > 0) {
			charsGenerator = NShrinkableGenerators.choose(validChars.value());
			characterGenerator = charsGenerator;
		}
		return Optional.ofNullable(charsGenerator);
	}

}
