package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

class FrequencyGenerator<T> extends ChooseRandomlyByFrequency<T> implements RandomGenerator<T> {

	FrequencyGenerator(List<? extends Tuple.Tuple2<Integer, ? extends T>> frequencies) {
		super(frequencies);
	}

	@Override
	public Shrinkable<T> next(Random random) {
		return new ChooseValueShrinkable<>(apply(random), possibleValues());
	}
}
