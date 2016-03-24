package net.jqwik;

import java.util.*;

public class BooleanGenerator implements Generator<Boolean> {

	private final Random random;

	public BooleanGenerator(Random random) {
		this.random = random;
	}

	@Override
	public Boolean generate() {
		return random.nextBoolean();
	}

	@Override
	public List<Boolean> shrink(Boolean value) {
		return null;
	}

	@Override
	public Optional<Long> finalNumberOfValues() {
		return Optional.of(2l);
	}

	@Override
	public List<Boolean> generateAll() {
		return Arrays.asList(new Boolean[] {true, false});
	}
}
