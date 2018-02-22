package net.jqwik.api.arbitraries;

public interface StringArbitrary extends NullableArbitrary<String> {
	StringArbitrary ofMaxLength(int maxLength);

	StringArbitrary ofMinLength(int minLength);

	default StringArbitrary ofLength(int length) {
		return ofMinLength(length).ofMaxLength(length);
	}

	StringArbitrary withChars(char[] chars);

	StringArbitrary withCharRange(char from, char to);

	StringArbitrary ascii();

	StringArbitrary alpha();

	StringArbitrary numeric();
}
