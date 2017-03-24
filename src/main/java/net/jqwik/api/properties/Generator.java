package net.jqwik.api.properties;

import javaslang.test.*;
import net.jqwik.execution.properties.*;
import net.jqwik.execution.properties.Combinators.*;

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

	static Arbitrary<String> string(char[] chars, int maxLength) {
		return new SizedArbitrary<>(string(chars), maxLength);
	}

	static <T extends Enum> Arbitrary<T> of(Class<T> enumClass) {
		return Arbitrary.of(enumClass.getEnumConstants());
	}

	static  <T1, T2> Combinator2<T1, T2> combine(Arbitrary<T1> a1, Arbitrary<T2> a2) {
		return Combinators.combine(a1, a2);
	}

	static  <T1, T2, T3> Combinator3<T1, T2, T3> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3) {
		return Combinators.combine(a1, a2, a3);
	}

	static  <T1, T2, T3, T4> Combinator4<T1, T2, T3, T4> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4) {
		return Combinators.combine(a1, a2, a3, a4);
	}

}
