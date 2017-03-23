package net.jqwik.api.properties;

import javaslang.test.Arbitrary;
import javaslang.test.Gen;
import net.jqwik.execution.properties.Combinators;
import net.jqwik.execution.properties.Combinators.Combinator3;
import net.jqwik.execution.properties.SizedArbitrary;

public interface Generator {

	static Arbitrary<String> string(char from, char to) {
		return Arbitrary.string(Gen.choose(from, to));
	}

	static Arbitrary<String> string(char from, char to, int maxLength) {
		return new SizedArbitrary<>(string(from, to), maxLength);
	}

	static Arbitrary<String> string(char[] chars) {
		return Arbitrary.string(Gen.choose(chars));
	}


	static <T extends Enum> Arbitrary<T> of(Class<T> enumClass) {
		return Arbitrary.of(enumClass.getEnumConstants());
	}

	static  <T1, T2, T3> Combinator3<T1, T2, T3> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3) {
		return Combinators.combine(a1, a2, a3);
	}
}
