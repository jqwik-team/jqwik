package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.support.*;

import static org.assertj.core.api.Assertions.*;

class DataBasedShrinkablesGeneratorTests {

	@Example
	void valuesFitParameters() {
		Iterable<Tuple.Tuple2<String, Integer>> data = Table.of(Tuple.of("a", 1), Tuple.of("b", 2));
		DataBasedShrinkablesGenerator shrinkablesGenerator = generator("stringAndInt", data);

		assertThat(nextValues(shrinkablesGenerator)).containsExactly("a", 1);
		assertThat(nextValues(shrinkablesGenerator)).containsExactly("b", 2);
		assertThat(shrinkablesGenerator.hasNext()).isFalse();
	}

	@Example
	void twoManyValues() {
		Iterable<Tuple.Tuple3<String, Integer, Boolean>> data = Table.of(Tuple.of("a", 1, true), Tuple.of("b", 2, false));
		DataBasedShrinkablesGenerator shrinkablesGenerator = generator("stringAndInt", data);

		assertThatThrownBy(shrinkablesGenerator::next).isInstanceOf(IncompatibleDataException.class);
	}

	@Example
	void twoFewValues() {
		Iterable<Tuple.Tuple1<String>> data = Table.of(Tuple.of("a"), Tuple.of("b"));
		DataBasedShrinkablesGenerator shrinkablesGenerator = generator("stringAndInt", data);

		assertThatThrownBy(shrinkablesGenerator::next).isInstanceOf(IncompatibleDataException.class);
	}

	@Example
	void valueTypesDontFit() {
		Iterable<Tuple.Tuple2<String, String>> data = Table.of(Tuple.of("a", "1"), Tuple.of("b", "2"));
		DataBasedShrinkablesGenerator shrinkablesGenerator = generator("stringAndInt", data);

		assertThatThrownBy(shrinkablesGenerator::next).isInstanceOf(IncompatibleDataException.class);
	}

	private List<Object> nextValues(DataBasedShrinkablesGenerator shrinkablesGenerator) {
		return values(shrinkablesGenerator.next());
	}

	private List<Object> values(List<Shrinkable<Object>> shrinkables) {
		return shrinkables.stream().map(objectShrinkable -> objectShrinkable.value()).collect(Collectors.toList());
	}

	private DataBasedShrinkablesGenerator generator(String methodName, Iterable<? extends Tuple> data) {
		PropertyMethodDescriptor methodDescriptor = createDescriptor(methodName);
		List<MethodParameter> parameters = TestHelper.getParameters(methodDescriptor);

		return new DataBasedShrinkablesGenerator(parameters, data);
	}

	private PropertyMethodDescriptor createDescriptor(String methodName) {
		return TestHelper.createPropertyMethodDescriptor(MyProperties.class, methodName, "0", 1000, 5, ShrinkingMode.FULL);
	}

	private static class MyProperties {

		public void stringAndInt(@ForAll String aString, @ForAll int anInt) {}
	}
}
