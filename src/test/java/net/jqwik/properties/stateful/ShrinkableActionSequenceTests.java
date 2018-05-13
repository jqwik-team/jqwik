package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.shrinking.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShrinkableActionSequenceTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;
	@SuppressWarnings("unchecked")
	private Consumer<ActionSequence<String>> valueReporter = mock(Consumer.class);
	private Consumer<FalsificationResult<ActionSequence<String>>> reporter = result -> valueReporter.accept(result.value());

	@Example
	void creation() {
		List<Shrinkable<Action<String>>> actions = asList(
			shrinkableAddString(),
			shrinkableAddX()
		);
		Shrinkable<ActionSequence<String>> shrinkable = new ShrinkableActionSequence<>(actions);

		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(2, 2, 4));

		SequentialActionSequence<String> sequence = (SequentialActionSequence<String>) shrinkable.value();
		assertThat(sequence.size()).isEqualTo(2);
	}

	@SuppressWarnings("unchecked")
	@Example
	void shrinkToEmptySequence() {
		List<Shrinkable<Action<String>>> actions = asList(
			shrinkableAddString(),
			shrinkableAddX(),
			shrinkableAddString(),
			shrinkableAddX()
		);
		Shrinkable<ActionSequence<String>> shrinkable = new ShrinkableActionSequence<>(actions);

		ShrinkingSequence<ActionSequence<String>> sequence = shrinkable.shrink(seq -> {
			seq.run("");
			return false;
		});
		assertThat(sequence.next(count, reporter)).isTrue();
		verify(valueReporter).accept(any(ActionSequence.class));
		assertThat(sequence.next(count, reporter)).isTrue();
		verify(valueReporter, times(2)).accept(any(ActionSequence.class));
		assertThat(sequence.next(count, reporter)).isTrue();
		verify(valueReporter, times(3)).accept(any(ActionSequence.class));
		assertThat(sequence.next(count, reporter)).isFalse();

		assertThat(sequence.current().value().size()).isEqualTo(1);
		assertThat(sequence.current().value().run("")).isEqualTo("x");

		assertThat(counter.get()).isEqualTo(3);
		verifyNoMoreInteractions(valueReporter);
	}

	@Example
	void alsoShrinkActions() {
		List<Shrinkable<Action<String>>> actions = asList(
			shrinkableAddString(),
			shrinkableAddX(),
			shrinkableAddString(),
			shrinkableAddString()
		);
		Shrinkable<ActionSequence<String>> shrinkable = new ShrinkableActionSequence<>(actions);

		ShrinkingSequence<ActionSequence<String>> sequence = shrinkable.shrink(seq -> {
			String result = seq.run("");
			return result.length() < 2;
		});

		while(sequence.next(count, reporter));

		assertThat(sequence.current().value().size()).isEqualTo(1);
		assertThat(sequence.current().value().run("")).isEqualTo("aa");
	}

	@Example
	void actionsWithFailingPreconditionsAreShrunkAway() {
		List<Shrinkable<Action<String>>> actions = asList(
			shrinkableFailingPrecondition(),
			shrinkableAddString(),
			shrinkableAddX(),
			shrinkableFailingPrecondition(),
			shrinkableAddString(),
			shrinkableFailingPrecondition(),
			shrinkableFailingPrecondition(),
			shrinkableAddString()
		);
		Shrinkable<ActionSequence<String>> shrinkable = new ShrinkableActionSequence<>(actions);

		ShrinkingSequence<ActionSequence<String>> sequence = shrinkable.shrink(seq -> {
			String result = seq.run("");
			return result.length() < 2;
		});

		while(sequence.next(count, reporter));

		assertThat(sequence.current().value().run("")).isEqualTo("aa");
	}

	@Property(tries = 100)
	void alwaysShrinkToSingleAction(@ForAll("stringActions") @Size(max = 50) List<Shrinkable<Action<String>>> actions) {
		actions.add(shrinkableAddX());
		Shrinkable<ActionSequence<String>> shrinkable = new ShrinkableActionSequence<>(actions);

		ShrinkingSequence<ActionSequence<String>> sequence = shrinkable.shrink(seq -> {
			String result = seq.run("");
			return !result.contains("x");
		});

		while(sequence.next(count, reporter));

		assertThat(sequence.current().value().run("")).isEqualTo("x");
	}

	@Provide
	Arbitrary<List<Shrinkable<Action<String>>>> stringActions() {
		return Arbitraries.of(shrinkableAddString(), shrinkableAddX(), shrinkableFailingPrecondition()).list();
	}

	private Shrinkable<Action<String>> shrinkableFailingPrecondition() {
		return Shrinkable.unshrinkable("poops")
						 .map(ignore -> new Action<String>() {
				@Override
				public boolean precondition(String model) {
					return false;
				}

				@Override
				public String run(String model) {
					return model;
				}
			});
	}

	private Shrinkable<Action<String>> shrinkableAddX() {
		return Shrinkable.unshrinkable("x")
						 .map(aString -> model -> model + aString);
	}

	private Shrinkable<Action<String>> shrinkableAddString() {
		return ShrinkableStringTests.createShrinkableString("cc", 2)
									.map(aString -> model -> model + aString);
	}

}
