package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

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

	@Example
	void nestedSequences() {
		ShrinkingSequence<Integer> first = new ShrinkingSequenceStub(asList(6, 5), IllegalArgumentException::new);
		ShrinkingSequence<Integer> second1 = new ShrinkingSequenceStub(asList(), IllegalArgumentException::new);
		ShrinkingSequence<Integer> second2 = new ShrinkingSequenceStub(asList(3), IllegalArgumentException::new);
		ShrinkingSequence<Integer> third = new ShrinkingSequenceStub(asList(), () -> null);

		ShrinkingSequence<Integer> sequence = first.andThen(ignore -> second1.andThen(i -> second2)).andThen(ignore -> third);

		assertThat(sequence.next(counter, reporter)).isTrue();
		assertThat(sequence.next(counter, reporter)).isTrue();
		assertThat(sequence.next(counter, reporter)).isTrue();
		assertThat(sequence.next(counter, reporter)).isFalse();

		assertThat(sequence.current().value()).isEqualTo(3);
		assertThat(sequence.current().throwable()).isPresent();
		assertThat(sequence.current().throwable().get()).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void anyNestedSequencesShrinkToEnd(@ForAll("nestedSequence") ShrinkingSequence<Integer> sequence) {
		while (sequence.next(counter, reporter)) { }

		assertThat(sequence.next(counter, reporter)).isFalse();
		assertThat(sequence.current().status()).isEqualTo(FalsificationResult.Status.FALSIFIED);
		assertThat(sequence.current().throwable()).isPresent();
	}

	@Provide
	Arbitrary<ShrinkingSequence<Integer>> nestedSequence() {
		Arbitrary<ShrinkingSequence<Integer>> start =
			Arbitraries.just(new ShrinkingSequenceStub(asList(1000), AssertionFailedError::new));
		Arbitrary<ShrinkingSequence<Integer>> next = recursiveSequence();
		return Combinators.combine(start, next).as((s, n) -> s.andThen(ignore -> n));
	}

	private Arbitrary<ShrinkingSequence<Integer>> recursiveSequence() {
		return Arbitraries.lazy(() -> Arbitraries.oneOf(
			Arbitraries.integers().between(1, 10).list().ofMaxSize(5).map(l -> new ShrinkingSequenceStub(l, AssertionFailedError::new)),
			Arbitraries.integers().between(21, 50).list().ofMaxSize(3).map(l -> new ShrinkingSequenceStub(l, AssertionFailedError::new)),
			Combinators.combine(recursiveSequence(), recursiveSequence()).as((b, n) -> b.andThen(ignore -> n))
		));
	}

	private static class ShrinkingSequenceStub implements ShrinkingSequence<Integer> {

		private final Iterator<Integer> iterator;
		private final Supplier<Throwable> throwableSupplier;
		private FalsificationResult<Integer> current;
		private String description;

		public ShrinkingSequenceStub(List<Integer> sequence, Supplier<Throwable> throwableSupplier) {
			this.iterator = sequence.iterator();
			this.throwableSupplier = throwableSupplier;
			this.description = JqwikStringSupport.displayString(sequence);
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

		@Override
		public void init(FalsificationResult<Integer> initialCurrent) {
			this.current = initialCurrent;
		}

		@Override
		public String toString() {
			return String.format("SequenceStub: %s", description);
		}
	}
}
