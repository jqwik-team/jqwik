package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.stateful.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShrinkableActionSequenceTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;
	private Consumer<ActionSequence<String>> reporter = mock(Consumer.class);

	@Example
	void creation() {
		List<NShrinkable<Action<String>>> actions = asList(
			shrinkableAddString(),
			shrinkableAddX()
		);
		NShrinkable<ActionSequence<String>> shrinkable = new NShrinkableActionSequence<>(actions);

		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(2, 2, 4));

		SequentialActionSequence<String> sequence = (SequentialActionSequence<String>) shrinkable.value();
		assertThat(sequence.size()).isEqualTo(2);
	}

	@Example
	void shrinkToEmptySequence() {
		List<NShrinkable<Action<String>>> actions = asList(
			shrinkableAddString(),
			shrinkableAddX(),
			shrinkableAddString(),
			shrinkableAddX()
		);
		NShrinkable<ActionSequence<String>> shrinkable = new NShrinkableActionSequence<>(actions);

		ShrinkingSequence<ActionSequence<String>> sequence = shrinkable.shrink(seq -> {
			seq.run("");
			return false;
		});
		assertThat(sequence.next(count, reporter)).isTrue();
		verify(reporter).accept(any(ActionSequence.class));
		assertThat(sequence.next(count, reporter)).isTrue();
		verify(reporter, times(2)).accept(any(ActionSequence.class));
		assertThat(sequence.next(count, reporter)).isTrue();
		verify(reporter, times(3)).accept(any(ActionSequence.class));
		assertThat(sequence.next(count, reporter)).isFalse();

		assertThat(sequence.current().value().size()).isEqualTo(1);
		assertThat(sequence.current().value().run("")).isEqualTo("x");

		assertThat(counter.get()).isEqualTo(3);
		verifyNoMoreInteractions(reporter);
	}

	@Example
	void alsoShrinkActions() {
		List<NShrinkable<Action<String>>> actions = asList(
			shrinkableAddString(),
			shrinkableAddX(),
			shrinkableAddString(),
			shrinkableAddString()
		);
		NShrinkable<ActionSequence<String>> shrinkable = new NShrinkableActionSequence<>(actions);

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
		List<NShrinkable<Action<String>>> actions = asList(
			shrinkableFailingPrecondition(),
			shrinkableAddString(),
			shrinkableAddX(),
			shrinkableFailingPrecondition(),
			shrinkableAddString(),
			shrinkableFailingPrecondition(),
			shrinkableFailingPrecondition(),
			shrinkableAddString()
		);
		NShrinkable<ActionSequence<String>> shrinkable = new NShrinkableActionSequence<>(actions);

		ShrinkingSequence<ActionSequence<String>> sequence = shrinkable.shrink(seq -> {
			String result = seq.run("");
			return result.length() < 2;
		});

		while(sequence.next(count, reporter));

		assertThat(sequence.current().value().run("")).isEqualTo("aa");
	}

	private NShrinkable<Action<String>> shrinkableFailingPrecondition() {
		return NShrinkable.unshrinkable("poops")
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

	private NShrinkable<Action<String>> shrinkableAddX() {
		return NShrinkable.unshrinkable("x")
			.map(aString -> model -> model + aString);
	}

	private NShrinkable<Action<String>> shrinkableAddString() {
		return ShrinkableStringTests.createShrinkableString("cc", 2)
			.map(aString -> model -> model + aString);
	}

}
