
package net.jqwik;

import java.util.*;

public class IntegerGenerator implements Generator<Integer> {

	private final Random random;

	public IntegerGenerator(Random random) {
		this.random = random;
	}

	@Override
	public Integer generate() {
		return random.nextInt();
	}

	@Override
	public List<Integer> shrink(Integer value) {
		if (value < 0) {
			return Arrays.asList(0, value / 2, value + 1);
		} else if (value > 0) {
			return Arrays.asList(0, value / 2, value - 1);
		}
		else
			return Collections.emptyList();
	}

	@Override
	public boolean canServeType(Class<?> type) {
		return type == Integer.class || type == int.class;
	}
}
