
package net.jqwik;

import java.util.Random;

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
	public Integer shrink(Integer value) {
		if (value < 0 || value > 0) {
			return value / 2;
		}
		else
			return value;
	}

	@Override
	public boolean canServeType(Class<?> type) {
		return type == Integer.class || type == int.class;
	}
}
