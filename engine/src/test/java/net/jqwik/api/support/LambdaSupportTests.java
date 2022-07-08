package net.jqwik.api.support;

import java.io.*;
import java.util.function.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class LambdaSupportTests {

	@Example
	void sameFunctionsAreEqual() {
		Function<String, String> f = s -> s;
		assertThat(LambdaSupport.areEqual(
			f,
			f
		)).isTrue();
	}

	@Example
	void separateFunctionsAreNotEqual() {
		Function<String, String> f = s -> s;
		Function<String, String> g = s -> s;
		assertThat(LambdaSupport.areEqual(
			f,
			g
		)).isFalse();
	}

	@Example
	void sameFunctionsWithImmutableClosureAreEqual() {
		int added = 5;
		Function<Integer, Integer> f = n -> n + added;
		assertThat(LambdaSupport.areEqual(
			f,
			f
		)).isTrue();
	}

	@Example
	void identityIsEqualToItself() {
		assertThat(LambdaSupport.areEqual(
			Function.identity(),
			Function.identity()
		)).isTrue();
	}

	@Example
	void serializableFunctionsCanBeCompared() {
		assertThat(LambdaSupport.areEqual(
			new SerializableAdder(5),
			new SerializableAdder(5)
		)).isTrue();

		assertThat(LambdaSupport.areEqual(
			new SerializableAdder(5),
			new SerializableAdder(6)
		)).isFalse();
	}

	private static class SerializableAdder implements Function<Integer, Integer>, Serializable {

		private final int added;

		private SerializableAdder(int added) {
			this.added = added;
		}

		@Override
		public Integer apply(Integer i) {
			return i + added;
		}
	}
}
