
package net.jqwik;

import java.util.*;
import java.util.stream.Stream;

import net.jqwik.api.Max;
import net.jqwik.api.Min;

public class IntegerGenerator implements Generator<Integer> {

	public static final int DEFAULT_MIN = Integer.MIN_VALUE;
	public static final int DEFAULT_MAX = Integer.MAX_VALUE - 1;

	private final Random random;

	private int min = DEFAULT_MIN;
	private int max = DEFAULT_MAX;

	public IntegerGenerator(Random random) {
		this.random = random;
	}

	@Override
	public Integer generate() {
		return random.ints(min, max + 1).findFirst().getAsInt();
	}

	@Override
	public Stream<Integer> generateAll() {
		return Stream.iterate(min, n -> n + 1).limit(numberOfValues());
	}

	@Override
	public List<Integer> shrink(Integer value) {
		if (value < 0) {
			return Arrays.asList(0, value / 2, value + 1);
		}
		else if (value > 0) {
			return Arrays.asList(0, value / 2, value - 1);
		}
		else
			return Collections.emptyList();
	}

	@Override
	public Optional<Long> finalNumberOfValues() {
		if (min != DEFAULT_MIN || max != DEFAULT_MAX) {
			long numberOfValues = numberOfValues();
			return Optional.of(numberOfValues);
		}
		return Optional.empty();
	}

	private long numberOfValues() {
		return (long) max - (long) min + 1;
	}

	public void configure(Min min) {
		this.min = (int) min.value();
	}

	public void configure(Max max) {
		this.max = (int) max.value();
	}

}
