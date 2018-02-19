package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

public interface StringArbitrary extends Arbitrary<String> {
	StringArbitrary withMaxLength(int maxLength);

	StringArbitrary withMinLength(int minLength);

	StringArbitrary withChars(char[] chars);

	StringArbitrary withChars(char from, char to);
}
