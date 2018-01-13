package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.*;
import java.util.stream.*;

public class StringArbitrary extends NullableArbitrary<String> {

	private Set<Character> allowedChars = new HashSet<>();
	private int minLength;
	private int maxLength;

	public StringArbitrary() {
		this(0, 0);
	}

	public StringArbitrary(char[] characters, int minLength, int maxLength) {
		this(minLength, maxLength);
		addAllowedChars(characters);
	}

	public StringArbitrary(char[] characters) {
		this(characters, 0, 0);
	}

	public StringArbitrary(char from, char to, int minLength, int maxLength) {
		this(minLength, maxLength);
		addAllowedChars(from, to);
	}

	public StringArbitrary(char from, char to) {
		this(from, to, 0, 0);
	}

	public StringArbitrary(int minLength, int maxLength) {
		super(String.class);
		this.minLength = minLength;
		this.maxLength = maxLength;
	}


	@SuppressWarnings("unchecked")
	@Override
	protected RandomGenerator<String> baseGenerator(int tries) {
		final int effectiveMaxLength = maxLength <= 0 ? Arbitrary.defaultMaxFromTries(tries) : maxLength;
		List<Shrinkable<String>> samples = Arrays.stream(new String[] { "" })
				.filter(s -> s.length() >= minLength && s.length() <= maxLength).map(s -> Shrinkable.unshrinkable(s))
				.collect(Collectors.toList());
		return RandomGenerators.strings(createCharacterGenerator(), minLength, effectiveMaxLength).withShrinkableSamples(samples);
	}

	public void configure(StringLength stringLength) {
		this.minLength = stringLength.min();
		this.maxLength = stringLength.max();
	}

	public void configure(Chars chars) {
		addAllowedChars(chars.value());
		addAllowedChars(chars.from(), chars.to());
	}

	private void addAllowedChars(char from, char to) {
		if (to >= from) {
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

	private RandomGenerator<Character> createCharacterGenerator() {
		if (allowedChars.isEmpty()) {
			addDefaultChars();
		}
		return RandomGenerators.choose(allowedChars.toArray(new Character[allowedChars.size()]));
	}

	private void addDefaultChars() {
		addAllowedChars('a', 'z');
		addAllowedChars('A', 'Z');
		addAllowedChars('0', '9');
		addAllowedChars(new char[] {' ', '@', ',', '.', ':', '-', '_'});
	}

}
