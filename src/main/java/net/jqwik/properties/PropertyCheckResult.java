package net.jqwik.properties;

import net.jqwik.support.*;

import java.util.*;

public interface PropertyCheckResult {

	enum Status {
		SATISFIED,
		FALSIFIED,
		ERRONEOUS,
		EXHAUSTED
	}

	Status status();

	String propertyName();

	/**
	 * The number of times a property has been tried including all tries rejected by a precondition aka assumption
	 */
	int countTries();

	/**
	 * The number of times a property has been actually been evaluated not counting the tries that were rejected by a
	 * precondition aka assumption
	 */
	int countChecks();

	long randomSeed();

	Optional<List<Object>> sample();

	Optional<List<Object>> originalSample();

	Optional<Throwable> throwable();

	abstract class ResultBase implements PropertyCheckResult {

		protected final Status status;
		protected final String propertyName;
		protected final int tries;
		protected final int checks;
		protected final long randomSeed;

		ResultBase(Status status, String propertyName, int tries, int checks, long randomSeed) {
			this.status = status;
			this.propertyName = propertyName;
			this.tries = tries;
			this.checks = checks;
			this.randomSeed = randomSeed;
		}

		@Override
		public String propertyName() {
			return propertyName;
		}

		@Override
		public Status status() {
			return status;
		}

		@Override
		public int countChecks() {
			return checks;
		}

		@Override
		public int countTries() {
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
		public Optional<List<Object>> originalSample() {
			return Optional.empty();
		}

		@Override
		public Optional<Throwable> throwable() {
			return Optional.empty();
		}

	}

	static PropertyCheckResult satisfied(String propertyName, int tries, int checks, long randomSeed) {
		return new ResultBase(Status.SATISFIED, propertyName, tries, checks, randomSeed) {
			@Override
			public String toString() {
				return String.format("Property [%s] satisfied", propertyName);
			}
		};
	}

	static PropertyCheckResult falsified(String propertyName, int tries, int checks, long randomSeed, List<Object> sample, List<Object> originalSample, Throwable throwable) {
		return new ResultBase(Status.FALSIFIED, propertyName, tries, checks, randomSeed) {
			@Override
			public Optional<List<Object>> sample() {
				return Optional.of(sample);
			}

			@Override
			public Optional<List<Object>> originalSample() {
				return Optional.of(originalSample);
			}

			@Override
			public String toString() {
				return String.format("Property [%s] falsified with sample %s", propertyName, JqwikStringSupport.displayString(sample));
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.ofNullable(throwable);
			}

		};
	}

	static PropertyCheckResult erroneous(String propertyName, int tries, int checks, long randomSeed, List<Object> sample,
			Throwable throwable) {
		return new ResultBase(Status.ERRONEOUS, propertyName, tries, checks, randomSeed) {
			@Override
			public Optional<List<Object>> sample() {
				return Optional.ofNullable(sample);
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.of(throwable);
			}

			@Override
			public String toString() {
				return String.format("Property [%s] erroneous with sample %s and exception [%s]", propertyName, sample, throwable);
			}
		};
	}

	static PropertyCheckResult exhausted(String propertyName, int tries, long randomSeed) {
		return new ResultBase(Status.EXHAUSTED, propertyName, tries, 0, randomSeed) {
			@Override
			public String toString() {
				return String.format("Property [%s] exhausted after [%d] tries", propertyName, tries);
			}
		};
	}

}
