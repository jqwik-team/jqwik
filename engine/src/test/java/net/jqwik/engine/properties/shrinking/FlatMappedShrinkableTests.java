package net.jqwik.engine.properties.shrinking;

import java.util.concurrent.atomic.*;
import java.util.function.*;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("FlatMappedShrinkable")
class FlatMappedShrinkableTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;

	@SuppressWarnings("unchecked")
	private Consumer<String> valueReporter = mock(Consumer.class);
	private Consumer<FalsificationResult<String>> reporter = result -> valueReporter.accept(result.value());

	@Example
	void creation(@ForAll long seed) {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(3);
		Function<Integer, Arbitrary<String>> flatMapper = anInt -> Arbitraries.strings().alpha().ofLength(anInt);
		Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);

		assertThat(shrinkable.distance().dimensions()).startsWith(ShrinkingDistance.of(3), ShrinkingDistance.of(3));
		assertThat(shrinkable.value()).hasSize(3);
	}

	@Property(tries = 50)
	void shrinkingEmbeddedShrinkable(@ForAll long seed) {
		//noinspection unchecked
		Mockito.reset(valueReporter);
		counter.set(0);

		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(4);
		Function<Integer, Arbitrary<String>> flatMapper = anInt -> Arbitraries.strings().alpha().ofLength(anInt);
		Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);

		ShrinkingSequence<String> sequence = shrinkable.shrinkWithCondition(ignore -> false);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).hasSize(3);
		verify(valueReporter).accept(ArgumentMatchers.argThat(aString -> aString.length() == 3));

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).hasSize(2);
		verify(valueReporter).accept(ArgumentMatchers.argThat(aString -> aString.length() == 2));

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).hasSize(1);
		verify(valueReporter).accept(ArgumentMatchers.argThat(aString -> aString.length() == 1));

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).hasSize(0);
		verify(valueReporter).accept(ArgumentMatchers.argThat(aString -> aString.length() == 0));

		assertThat(counter.get()).isEqualTo(4);
	}

	@Example
	void alsoShrinkResultOfArbitraryEvaluation(@ForAll long seed) {
		Shrinkable<Integer> integerShrinkable = new OneStepShrinkable(4);
		Function<Integer, Arbitrary<String>> flatMapper = anInt -> Arbitraries.strings().withCharRange('a', 'z').ofLength(anInt);
		Shrinkable<String> shrinkable = integerShrinkable.flatMap(flatMapper, 1000, seed);
		assertThat(shrinkable.value()).hasSize(4);

		ShrinkingSequence<String> sequence = shrinkable.shrinkWithCondition(aString -> aString.length() < 3);

		while(sequence.next(count, reporter));

		assertThat(sequence.current().value()).isEqualTo("aaa");
	}


}
