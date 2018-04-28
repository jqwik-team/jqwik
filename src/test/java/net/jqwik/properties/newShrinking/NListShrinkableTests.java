package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;
import net.jqwik.properties.newShrinking.ShrinkableTypesForTest.*;
import org.assertj.core.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

class NListShrinkableTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;

	@Example
	void shrinkDownAllTheWay() {
		NShrinkable<List<Integer>> shrinkable = createListShrinkable(0, 1, 2);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(3, 3));
		assertThat(shrinkable.value()).isEqualTo(asList(0, 1, 2));

		ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(aList -> false);
		assertThat(sequence.current()).isEqualTo(shrinkable);

		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value().size()).isEqualTo(2);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value().size()).isEqualTo(1);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value().size()).isEqualTo(0);
		assertThat(sequence.next(count)).isFalse();

		Assertions.assertThat(counter.get()).isEqualTo(3);
	}

	@Example
	void shrinkDownToOneElement() {
		NShrinkable<List<Integer>> shrinkable = createListShrinkable(0, 1, 2);

		ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(List::isEmpty);
		assertThat(sequence.current()).isEqualTo(shrinkable);

		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value().size()).isEqualTo(2);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value().size()).isEqualTo(1);
		assertThat(sequence.next(count)).isFalse();

		Assertions.assertThat(counter.get()).isEqualTo(2);
	}

	@Example
	void alsoShrinkElements() {
		NShrinkable<List<Integer>> shrinkable = createListShrinkable(1, 1, 1);

		ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(integers -> integers.size() <= 1);
		assertThat(sequence.current()).isEqualTo(shrinkable);

		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(asList(1, 1));
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value().size()).isEqualTo(2);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value().size()).isEqualTo(2);
		assertThat(sequence.next(count)).isFalse();
		assertThat(sequence.current().value()).isEqualTo(asList(0, 0));

		Assertions.assertThat(counter.get()).isEqualTo(3);
	}

	@Example
	void alsoShrinkWithFilter() {
		NShrinkable<List<Integer>> shrinkable = createListShrinkable(3, 3, 3);

		Falsifier<List<Integer>> falsifier = List::isEmpty;
		Falsifier<List<Integer>> filteredFalsifier = falsifier.withFilter(
			elements -> elements.stream().allMatch(i -> i % 2 == 1));
		ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(filteredFalsifier);
		assertThat(sequence.current()).isEqualTo(shrinkable);

		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(asList(3, 3));
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(asList(3));
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(asList(3));
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(asList(1));
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(asList(1));
		assertThat(sequence.next(count)).isFalse();

		Assertions.assertThat(counter.get()).isEqualTo(5);
	}


	@Example
	void longList() {
		List<NShrinkable<Integer>> elementShrinkables =
			IntStream.range(1, 200).mapToObj(OneStepShrinkable::new).collect(Collectors.toList());
		NShrinkable<List<Integer>> shrinkable = new NListShrinkable<>(elementShrinkables);

		Falsifier<List<Integer>> falsifier = List::isEmpty;
		ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(falsifier);
		assertThat(sequence.current()).isEqualTo(shrinkable);

		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(100);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(50);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(25);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(13);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(9);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(8);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(7);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(6);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(5);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(4);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(3);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(2);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(1);
		assertThat(sequence.next(count)).isTrue();
		assertThat(sequence.current().value()).hasSize(1);
		assertThat(sequence.next(count)).isFalse();

		Assertions.assertThat(counter.get()).isEqualTo(14);
	}

	private NShrinkable<List<Integer>> createListShrinkable(Integer... listValues) {
		List<NShrinkable<Integer>> elementShrinkables =
			Arrays.stream(listValues).map(OneStepShrinkable::new).collect(Collectors.toList());
		return new NListShrinkable<>(elementShrinkables);
	}

}
