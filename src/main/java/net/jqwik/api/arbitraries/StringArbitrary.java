package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

public interface StringArbitrary extends Arbitrary<String> {
	StringArbitrary withLength(int min, int max);

	StringArbitrary withChars(char[] chars);

	StringArbitrary withChars(char from, char to);

}
