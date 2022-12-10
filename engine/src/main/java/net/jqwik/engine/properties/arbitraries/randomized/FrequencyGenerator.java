package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.JqwikRandom;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

class FrequencyGenerator<T> extends ChooseRandomlyByFrequency<T> implements RandomGenerator<T> {

	FrequencyGenerator(List<Tuple.Tuple2<Integer, T>> frequencies) {
		super(frequencies);
	}

	@Override
	public Shrinkable<T> next(JqwikRandom random) {
		return new ChooseValueShrinkable<>(apply(random), possibleValues());
	}
}
