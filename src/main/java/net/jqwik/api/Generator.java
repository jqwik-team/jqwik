package net.jqwik.api;

import net.jqwik.properties.*;
import net.jqwik.properties.Combinators.*;

import java.util.*;
import java.util.stream.*;

public interface Generator {

	static Arbitrary<Integer> integer(int min, int max) {
		return Arbitraries.integer(min, max);
	}

	static Arbitrary<Long> integer(long min, long max) {
		return Arbitraries.longInteger(min, max);
	}

	static Arbitrary<String> string(char from, char to) {
		return Arbitraries.string(from, to);
	}

	static Arbitrary<String> string(char from, char to, int maxLength) {
		return Arbitraries.string(from, to, maxLength);
	}

	static Arbitrary<String> string(char[] chars) {
		return Arbitraries.string(chars);
	}

	static Arbitrary<String> string(char[] chars, int maxLength) {
		return Arbitraries.string(chars, maxLength);
	}

	static <T extends Enum> Arbitrary<T> of(Class<T> enumClass) {
		return Arbitraries.of(enumClass);
	}

	@SafeVarargs
	static <U> Arbitrary<U> of(U... values) {
		return Arbitraries.of(values);
	}

	static <T> Arbitrary<Set<T>> setOf(Arbitrary<T> arbitraryT) {
		return Arbitraries.setOf(arbitraryT);
	}

	static <T> Arbitrary<List<T>> listOf(Arbitrary<T> arbitraryT) {
		return Arbitraries.listOf(arbitraryT);
	}

	static <T> Arbitrary<Stream<T>> streamOf(Arbitrary<T> arbitraryT) {
		return Arbitraries.streamOf(arbitraryT);
	}

	static <T1, T2> Combinator2<T1, T2> combine(Arbitrary<T1> a1, Arbitrary<T2> a2) {
		return Combinators.combine(a1, a2);
	}

	static <T1, T2, T3> Combinator3<T1, T2, T3> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3) {
		return Combinators.combine(a1, a2, a3);
	}

	static <T1, T2, T3, T4> Combinator4<T1, T2, T3, T4> combine(Arbitrary<T1> a1, Arbitrary<T2> a2, Arbitrary<T3> a3, Arbitrary<T4> a4) {
		return Combinators.combine(a1, a2, a3, a4);
	}

	static <T> Arbitrary<Optional<T>> optionalOf(Arbitrary<T> a1) {
		return Arbitraries.optionalOf(a1);
	}
}
