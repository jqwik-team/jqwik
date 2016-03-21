
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
	public boolean canServeType(Class<?> type) {
		return type == Integer.class || type == int.class;
	}
}
