package net.jqwik.api.arbitraries;

public interface StringArbitrary extends NullableArbitrary<String> {
	StringArbitrary withMaxLength(int maxLength);

	StringArbitrary withMinLength(int minLength);

	StringArbitrary withChars(char[] chars);

	StringArbitrary withChars(char from, char to);
}
