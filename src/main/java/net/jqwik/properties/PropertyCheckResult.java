package net.jqwik.properties;

import java.util.*;

public interface PropertyCheckResult {

	enum Status {
		SATISFIED, FALSIFIED, ERRONEOUS, EXHAUSTED
	}

	Status status();

	String propertyName();

	/**
	 * The number of times a property has been tried including all tries
	 * rejected by a precondition aka assumption
	 */
	int countTries();

	/**
	 * The number of times a property has been actually been evaluated
	 * not counting the tries that were rejected by a precondition aka assumption
	 */
	int countChecks();

	long randomSeed();

	Optional<List<Object>> sample();

	Optional<Throwable> throwable();

	static PropertyCheckResult satisfied(String propertyName, int tries, int checks, long randomSeed) {
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
			public int countTries() {
				return tries;
			}

			@Override
			public int countChecks() {
				return checks;
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

	static PropertyCheckResult falsified(String propertyName, int tries, int checks, long randomSeed, List<Object> sample) {
		return new PropertyCheckResult() {
			@Override
			public Status status() {
				return Status.FALSIFIED;
			}

			@Override
			public String propertyName() {
				return propertyName;
			}

			@Override
			public int countTries() {
				return tries;
			}

			@Override
			public int countChecks() {
				return checks;
			}

			@Override
			public long randomSeed() {
				return randomSeed;
			}

			@Override
			public Optional<List<Object>> sample() {
				return Optional.of(sample);
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.empty();
			}
		};
	}

	static PropertyCheckResult erroneous(String propertyName, int tries, int checks, long randomSeed, List<Object> sample, Throwable throwable) {
		return new PropertyCheckResult() {
			@Override
			public Status status() {
				return Status.ERRONEOUS;
			}

			@Override
			public String propertyName() {
				return propertyName;
			}

			@Override
			public int countTries() {
				return tries;
			}

			@Override
			public int countChecks() {
				return checks;
			}

			@Override
			public long randomSeed() {
				return randomSeed;
			}

			@Override
			public Optional<List<Object>> sample() {
				return Optional.ofNullable(sample);
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.of(throwable);
			}
		};
	}

	static PropertyCheckResult exhausted(String propertyName, int tries, long randomSeed) {
		return new PropertyCheckResult() {
			@Override
			public Status status() {
				return Status.EXHAUSTED;
			}

			@Override
			public String propertyName() {
				return propertyName;
			}

			@Override
			public int countTries() {
				return tries;
			}

			@Override
			public int countChecks() {
				return 0;
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
