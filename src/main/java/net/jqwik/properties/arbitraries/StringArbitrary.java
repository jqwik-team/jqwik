package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.Collectors;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.constraints.*;
import net.jqwik.properties.*;

public class StringArbitrary extends NullableArbitrary<String> {

	private final static char[] defaultChars = { 'a', 'b', 'y', 'z', 'A', 'B', 'Y', 'Z', '0', '9', ' ', ',', '.', '!', '@' };

	private RandomGenerator<Character> characterGenerator;
	private int minLength;
	private int maxLength;

	public StringArbitrary() {
		this(defaultGenerator(), 0, 0);
	}

	private static RandomGenerator<Character> defaultGenerator() {
		return RandomGenerators.choose(defaultChars);
	}

	public StringArbitrary(RandomGenerator<Character> characterGenerator, int minLength, int maxLength) {
		super(String.class);
		this.characterGenerator = characterGenerator;
		this.minLength = minLength;
		this.maxLength = maxLength;
	}

	public StringArbitrary(char[] characters, int minLength, int maxLength) {
		this(createGenerator(characters), minLength, maxLength);
	}

	public StringArbitrary(char[] characters) {
		this(characters, 0, 0);
	}

	private static RandomGenerator<Character> createGenerator(char[] characters) {
		return RandomGenerators.choose(characters);
	}

	public StringArbitrary(char from, char to, int minSize, int maxLength) {
		this(createGenerator(from, to), minSize, maxLength);
	}

	public StringArbitrary(char from, char to) {
		this(from, to, 0, 0);
	}

	private static RandomGenerator<Character> createGenerator(char from, char to) {
		return RandomGenerators.choose(from, to);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected RandomGenerator<String> baseGenerator(int tries) {
		final int effectiveMaxLength = maxLength <= 0 ? Arbitrary.defaultMaxFromTries(tries) : maxLength;
		List<Shrinkable<String>> samples = Arrays.stream(new String[] { "" })
				.filter(s -> s.length() >= minLength && s.length() <= effectiveMaxLength).map(s -> Shrinkable.unshrinkable(s))
				.collect(Collectors.toList());
		return RandomGenerators.string(characterGenerator, minLength, effectiveMaxLength).withShrinkableSamples(samples);
	}

	public void configure(StringLength stringLength) {
		this.minLength = stringLength.min();
		this.maxLength = stringLength.max();
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

	private Optional<RandomGenerator<Character>> mix(Optional<RandomGenerator<Character>> charsGenerator, //
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
