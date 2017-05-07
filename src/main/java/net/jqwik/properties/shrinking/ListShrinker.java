package net.jqwik.properties.shrinking;

import net.jqwik.properties.*;

import java.util.*;

public class ListShrinker<T> implements Shrinker<List<T>> {
	private final Arbitrary<T> elementArbitrary;

	public ListShrinker(Arbitrary<T> elementArbitrary) {
		this.elementArbitrary = elementArbitrary;
	}

	@Override
	public Shrinkable<List<T>> shrink(List<T> list) {
		ShrinkableChoice<List<T>> choice = new ShrinkableChoice<>();
		choice.addChoice(shrinkableValueOf(list));
		choice.addChoice(shrinkFromTail(list));
		choice.addChoice(shrinkFromHead(list));
		return new ShrinkableList<>(choice, elementArbitrary);
	}

	private ShrinkableSequence<List<T>> shrinkFromTail(List<T> list) {
		ShrinkableSequence<List<T>> sequence = new ShrinkableSequence<>();
		List<T> current = new ArrayList<>(list);
		while(!current.isEmpty()) {
			current.remove(current.size() - 1);
			addShrinkStep(sequence, current);
		}
		return sequence;
	}

	private ShrinkableSequence<List<T>> shrinkFromHead(List<T> list) {
		ShrinkableSequence<List<T>> sequence = new ShrinkableSequence<>();
		List<T> current = new ArrayList<>(list);
		while(!current.isEmpty()) {
			current.remove(0);
			addShrinkStep(sequence, current);
		}
		return sequence;
	}

	private void addShrinkStep(ShrinkableSequence<List<T>> shrinkableList, List<T> current) {
		shrinkableList.addStep(shrinkableValueOf(current));
	}

	private ShrinkableValue<List<T>> shrinkableValueOf(List<T> current) {
		return ShrinkableValue.of(new ArrayList<>(current), current.size());
	}
}
