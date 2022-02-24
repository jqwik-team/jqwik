package net.jqwik.engine.support;

import java.util.*;

import net.jqwik.api.*;

public class ChooseRandomlyByFrequency<T> {

	private final Map<T, Integer> upperBorders = new HashMap<>();
	private int size = 0;
	private List<T> valuesToChooseFrom;

	public ChooseRandomlyByFrequency(List<Tuple.Tuple2<Integer, T>> frequencies) {
		calculateUpperBorders(frequencies);
		if (size <= 0) {
			throw new JqwikException(String.format(
				"%s does not contain any positive frequencies.",
				JqwikStringSupport.displayString(frequencies)
			));
		}
	}

	protected List<T> possibleValues() {
		return valuesToChooseFrom;
	}

	private void calculateUpperBorders(List<Tuple.Tuple2<Integer, T>> frequencies) {
		List<T> values = new ArrayList<>();
		for (Tuple.Tuple2<Integer, T> tuple : frequencies) {
			int frequency = tuple.get1();
			if (frequency <= 0)
				continue;
			size += frequency;
			T value = tuple.get2();
			values.add(value);
			upperBorders.put(value, size);
		}
		valuesToChooseFrom = values;
	}

	private T choose(int index) {
		T currentChoice = null;
		for (T key : upperBorders.keySet()) {
			int upper = upperBorders.get(key);
			if (upper > index) {
				if (currentChoice == null) {
					currentChoice = key;
				} else if (upper < upperBorders.get(currentChoice)) {
					currentChoice = key;
				}
			}
		}
		return currentChoice;
	}

	public T choose(Random random) {
		return choose(random.nextInt(size));
	}
}
