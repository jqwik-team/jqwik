package net.jqwik.properties;

import java.util.concurrent.atomic.*;

public class CountingArbitrary implements Arbitrary<Integer> {

	private final AtomicInteger count = new AtomicInteger(0);
	private final Generator<Integer> countingGenerator = () -> count.incrementAndGet();

	@Override
	public Generator<Integer> generator(long seed, int tries) {
		return countingGenerator;
	}

	public int count() {
		return count.get();
	}
}
