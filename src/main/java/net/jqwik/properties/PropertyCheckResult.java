package net.jqwik.properties;

import java.util.*;

public interface PropertyCheckResult {

	enum Status {
		SATISFIED, FALSIFIED, ERRONEOUS, EXHAUSTED
	}

	Status status();

	String propertyName();

	int tries();

	long randomSeed();

	Optional<List<Object>> sample();

	Optional<Throwable> throwable();

	static PropertyCheckResult satisfied(String propertyName, int tries, long randomSeed) {
		return new PropertyCheckResult() {
			@Override
			public Status status() {
				return Status.SATISFIED;
			}

			@Override
			public String propertyName() {
				return propertyName;
			}

			@Override
			public int tries() {
				return tries;
			}

			@Override
			public long randomSeed() {
				return randomSeed;
			}

			@Override
			public Optional<List<Object>> sample() {
				return Optional.empty();
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.empty();
			}
		};
	}

}
