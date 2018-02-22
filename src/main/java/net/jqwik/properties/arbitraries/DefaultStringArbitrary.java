package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultStringArbitrary extends NullableArbitraryBase<String> implements StringArbitrary {

	private Set<Character> allowedChars = new HashSet<>();
	private int minLength = 0;
	private int maxLength = 0;

	public DefaultStringArbitrary() {
		super(String.class);
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

	@Override
	public StringArbitrary ofMinLength(int minLength) {
		DefaultStringArbitrary clone = typedClone();
		clone.minLength = minLength;
		return clone;
	}

	@Override
	public StringArbitrary ofMaxLength(int maxLength) {
		DefaultStringArbitrary clone = typedClone();
		clone.maxLength = maxLength;
		return clone;
	}

	@Override
	public StringArbitrary withChars(char[] chars) {
		DefaultStringArbitrary clone = typedClone();
		clone.addAllowedChars(chars);
		return clone;
	}

	@Override
	public StringArbitrary withChars(char from, char to) {
		if (from == 0 && to == 0) {
			return this;
		}
		DefaultStringArbitrary clone = typedClone();
		clone.addAllowedChars(from, to);
		return clone;
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
		addAllowedChars(new char[] { ' ', '@', ',', '.', ':', '-', '_' });
	}

}
