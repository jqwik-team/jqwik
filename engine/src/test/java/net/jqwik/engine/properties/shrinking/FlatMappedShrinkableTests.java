package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.function.*;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("FlatMappedShrinkable")
class FlatMappedShrinkableTests {

	@SuppressWarnings("unchecked")
	private final Consumer<String> valueReporter = mock(Consumer.class);

	@Example
	void creation(@ForAll long seed) {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Function<Integer, Arbitrary<String>> flatMapper = anInt -> Arbitraries.strings().alpha().ofLength(anInt);
		Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);

		assertThat(shrinkable.distance().dimensions()).startsWith(ShrinkingDistance.of(3), ShrinkingDistance.of(3));
		assertThat(shrinkable.value()).hasSize(3);
	}

	@Property(tries = 50, shrinking = ShrinkingMode.OFF)
	void shrinkingEmbeddedShrinkable(@ForAll long seed) {
		Assume.that(seed != 0L);

		//noinspection unchecked
		Mockito.reset(valueReporter);

		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(4);
		Function<Integer, Arbitrary<String>> flatMapper = anInt -> Arbitraries.strings().withCharRange('a', 'z').ofLength(anInt);
		Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);

		Falsifier<String> falsifier = ignore -> TryExecutionResult.falsified(null);
		String shrunkValue = ShrinkingTestHelper.shrinkToEnd(shrinkable, falsifier, valueReporter, null);

		verify(valueReporter).accept(ArgumentMatchers.argThat(aString -> aString.length() == 3));
		verify(valueReporter).accept(ArgumentMatchers.argThat(aString -> aString.length() == 2));
		verify(valueReporter).accept(ArgumentMatchers.argThat(aString -> aString.length() == 1));
		verify(valueReporter).accept(ArgumentMatchers.argThat(aString -> aString.length() == 0));

		assertThat(shrunkValue).isEqualTo("");
	}

	@Example
	void alsoShrinkResultOfArbitraryEvaluation(@ForAll long seed) {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(4);
		Function<Integer, Arbitrary<String>> flatMapper = anInt -> Arbitraries.strings().withCharRange('a', 'z').ofLength(anInt);
		Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);
		assertThat(shrinkable.value()).hasSize(4);

		TestingFalsifier<String> falsifier = aString -> aString.length() < 3;
		String shrunkValue = ShrinkingTestHelper.shrinkToEnd(shrinkable, falsifier, null);
		assertThat(shrunkValue).isEqualTo("aaa");
	}

	@Example
	void innerShrinkableIsMoreImportantWhileShrinking() {
		Shrinkable<Integer> integerShrinkable = new ShrinkableBigInteger(
			BigInteger.valueOf(5),
			Range.of(BigInteger.ONE, BigInteger.TEN),
			BigInteger.ONE
		).map(BigInteger::intValueExact);

		Function<Integer, Arbitrary<String>> flatMapper = i -> Arbitraries.strings().withCharRange('a', 'z').ofLength(i);
		Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, 42L);
		assertThat(shrinkable.value()).hasSize(5);

		TestingFalsifier<String> falsifier = aString -> aString.length() < 3;
		String shrunkValue = ShrinkingTestHelper.shrinkToEnd(shrinkable, falsifier, null);
		assertThat(shrunkValue).isEqualTo("aaa");
	}

}
