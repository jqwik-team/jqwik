package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.properties.shrinking.*;
import net.jqwik.support.*;

public class FrequencyGenerator<T> implements RandomGenerator<T> {

	private final Map<T, Integer> upperBorders = new HashMap<>();
	private int size = 0;
	private List<T> valuesToChooseFrom;

	FrequencyGenerator(Tuple.Tuple2<Integer, T>[] frequencies) {
		calculateUpperBorders(frequencies);
		if (size <= 0) {
			throw new JqwikException(String.format("%s does not contain any positive frequencies.", JqwikStringSupport.displayString(frequencies)));
		}
	}

	private void calculateUpperBorders(Tuple.Tuple2<Integer, T>[] frequencies) {
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

	@Override
	public Shrinkable<T> next(Random random) {
		int index = random.nextInt(size);
		return new ChooseValueShrinkable<>(choose(index), valuesToChooseFrom);
	}
}
