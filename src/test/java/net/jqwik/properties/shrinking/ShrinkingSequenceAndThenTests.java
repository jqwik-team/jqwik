package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

class ShrinkingSequenceAndThenTests {

	private Runnable counter = () -> {};
	private Consumer<FalsificationResult<Integer>> reporter = ignore -> {};

	@Example
	void shrinkingThroughToFollowUpSequence() {

		ShrinkingSequence<Integer> first = new ShrinkingSequenceStub(asList(4, 3), IllegalArgumentException::new);
		ShrinkingSequence<Integer> second = new ShrinkingSequenceStub(asList(2, 1), () -> null);

		ShrinkingSequence<Integer> sequence = first.andThen(ignore -> second);

		assertThat(sequence.next(counter, reporter)).isTrue();

		assertThat(sequence.current().value()).isEqualTo(4);
		assertThat(sequence.current().throwable()).isPresent();
		assertThat(sequence.current().throwable().get()).isInstanceOf(IllegalArgumentException.class);

		assertThat(sequence.next(counter, reporter)).isTrue();
		assertThat(sequence.next(counter, reporter)).isTrue();
		assertThat(sequence.next(counter, reporter)).isTrue();

		assertThat(sequence.next(counter, reporter)).isFalse();

		assertThat(sequence.current().value()).isEqualTo(1);
		assertThat(sequence.current().throwable()).isNotPresent();
	}

	@Example
	void followUpSequenceCannotShrinkMore() {
		ShrinkingSequence<Integer> first = new ShrinkingSequenceStub(asList(4, 3), IllegalArgumentException::new);
		ShrinkingSequence<Integer> second = new ShrinkingSequenceStub(asList(), () -> null);

		ShrinkingSequence<Integer> sequence = first.andThen(ignore -> second);

		assertThat(sequence.next(counter, reporter)).isTrue();
		assertThat(sequence.next(counter, reporter)).isTrue();
		assertThat(sequence.next(counter, reporter)).isFalse();

		assertThat(sequence.current().value()).isEqualTo(3);
		assertThat(sequence.current().throwable()).isPresent();
		assertThat(sequence.current().throwable().get()).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void twoFollowUpSequencesCannotShrinkMore() {
		ShrinkingSequence<Integer> first = new ShrinkingSequenceStub(asList(4, 3), IllegalArgumentException::new);
		ShrinkingSequence<Integer> second = new ShrinkingSequenceStub(asList(), () -> null);
		ShrinkingSequence<Integer> third = new ShrinkingSequenceStub(asList(), () -> null);

		ShrinkingSequence<Integer> sequence = first.andThen(ignore -> second).andThen(ignore -> third);

		assertThat(sequence.next(counter, reporter)).isTrue();
		assertThat(sequence.next(counter, reporter)).isTrue();
		assertThat(sequence.next(counter, reporter)).isFalse();

		assertThat(sequence.current().value()).isEqualTo(3);
		assertThat(sequence.current().throwable()).isPresent();
		assertThat(sequence.current().throwable().get()).isInstanceOf(IllegalArgumentException.class);
	}

	@Example
	void middleSequenceCannotShrink() {
		ShrinkingSequence<Integer> first = new ShrinkingSequenceStub(asList(4, 3), IllegalArgumentException::new);
		ShrinkingSequence<Integer> second = new ShrinkingSequenceStub(asList(), () -> null);
		ShrinkingSequence<Integer> third = new ShrinkingSequenceStub(asList(2, 1), () -> null);

		ShrinkingSequence<Integer> sequence = first.andThen(ignore -> second).andThen(ignore -> third);

		assertThat(sequence.next(counter, reporter)).isTrue();
		assertThat(sequence.next(counter, reporter)).isTrue();
		assertThat(sequence.next(counter, reporter)).isTrue();
		assertThat(sequence.next(counter, reporter)).isTrue();
		assertThat(sequence.next(counter, reporter)).isFalse();

		assertThat(sequence.current().value()).isEqualTo(1);
		assertThat(sequence.current().throwable()).isNotPresent();
	}

	private static class ShrinkingSequenceStub implements ShrinkingSequence<Integer> {

		private final Iterator<Integer> iterator;
		private final Supplier<Throwable> throwableSupplier;
		private FalsificationResult<Integer> current;

		public ShrinkingSequenceStub(List<Integer> sequence, Supplier<Throwable> throwableSupplier) {
			this.iterator = sequence.iterator();
			this.throwableSupplier = throwableSupplier;
		}

		@Override
		public boolean next(Runnable count, Consumer<FalsificationResult<Integer>> falsifiedReporter) {
			boolean hasNext = iterator.hasNext();
			if (hasNext) {
				this.current = FalsificationResult.falsified(Shrinkable.unshrinkable(iterator.next()), throwableSupplier.get());
			}
			return hasNext;
		}

		@Override
		public FalsificationResult<Integer> current() {
			return current;
		}
	}
}
