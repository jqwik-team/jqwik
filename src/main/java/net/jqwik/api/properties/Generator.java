package net.jqwik.api.properties;

import javaslang.test.Arbitrary;
import javaslang.test.Gen;

public interface Generator {

	static Arbitrary<String> string(char from, char to) {
		return Arbitrary.string(Gen.choose(from, to));
	}

	static Arbitrary<String> string(char[] chars) {
		return Arbitrary.string(Gen.choose(chars));
	}
}
