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
		if (value)
			return new ArrayList<>();
		return Arrays.asList(true);
	}

	@Override
	public Optional<Long> finalNumberOfValues() {
		return Optional.of(2L);
	}

	@Override
	public List<Boolean> generateAll() {
		return Arrays.asList(true, false);
	}
}
