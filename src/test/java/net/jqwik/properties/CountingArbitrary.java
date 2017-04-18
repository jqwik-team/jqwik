package net.jqwik.properties;

import java.util.concurrent.atomic.*;

public class CountingArbitrary implements Arbitrary<Integer> {

	private final AtomicInteger count = new AtomicInteger(0);
	private final RandomGenerator<Integer> countingGenerator = random -> count.incrementAndGet();

	@Override
	public RandomGenerator<Integer> generator(long seed, int tries) {
		return countingGenerator;
	}

	public int count() {
		return count.get();
	}
}
