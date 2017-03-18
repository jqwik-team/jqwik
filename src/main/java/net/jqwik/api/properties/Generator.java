package net.jqwik.api.properties;

import javaslang.test.Arbitrary;
import javaslang.test.Gen;

public interface Generator {

	static Arbitrary<String> string(char min, char max) {
		return Arbitrary.string(Gen.choose(min, max));
	}

	static Arbitrary<String> string(char[] chars) {
		return Arbitrary.string(Gen.choose(chars));
	}
}
