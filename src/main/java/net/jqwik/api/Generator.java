package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.properties.*;
import net.jqwik.properties.NCombinators.*;

public interface Generator {

	static NArbitrary<Integer> integer(int min, int max) {
		return NArbitraries.integer(min, max);
	}

	static NArbitrary<Long> integer(long min, long max) {
		return NArbitraries.longInteger(min, max);
	}

	static NArbitrary<String> string(char from, char to) {
		return NArbitraries.string(from, to);
	}

	static NArbitrary<String> string(char from, char to, int maxLength) {
		return NArbitraries.string(from, to, maxLength);
	}

	static NArbitrary<String> string(char[] chars) {
		return NArbitraries.string(chars);
	}

	static NArbitrary<String> string(char[] chars, int maxLength) {
		return NArbitraries.string(chars, maxLength);
	}

	static <T extends Enum> NArbitrary<T> of(Class<T> enumClass) {
		return NArbitraries.of(enumClass);
	}

	@SafeVarargs
	static <U> NArbitrary<U> of(U... values) {
		return NArbitraries.of(values);
	}

	static <T> NArbitrary<Set<T>> setOf(NArbitrary<T> arbitraryT) {
		return NArbitraries.setOf(arbitraryT);
	}

	static <T> NArbitrary<List<T>> listOf(NArbitrary<T> arbitraryT) {
		return NArbitraries.listOf(arbitraryT);
	}

	static <T> NArbitrary<Stream<T>> streamOf(NArbitrary<T> arbitraryT) {
		return NArbitraries.streamOf(arbitraryT);
	}

	static <T1, T2> ACombinator2<T1, T2> combine(NArbitrary<T1> a1, NArbitrary<T2> a2) {
		return NCombinators.combine(a1, a2);
	}

	static <T1, T2, T3> Combinator3<T1, T2, T3> combine(NArbitrary<T1> a1, NArbitrary<T2> a2, NArbitrary<T3> a3) {
		return NCombinators.combine(a1, a2, a3);
	}

	static <T1, T2, T3, T4> Combinator4<T1, T2, T3, T4> combine(NArbitrary<T1> a1, NArbitrary<T2> a2, NArbitrary<T3> a3,
			NArbitrary<T4> a4) {
		return NCombinators.combine(a1, a2, a3, a4);
	}

	static <T> NArbitrary<Optional<T>> optionalOf(NArbitrary<T> a1) {
		return NArbitraries.optionalOf(a1);
	}
}
