package net.jqwik.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.support.*;

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
	 * @return The number of times a property has been tried including all tries rejected by a precondition aka assumption
	 */
	int countTries();

	/**
	 * @return The number of times a property has actually been evaluated not counting the tries that were rejected by a
	 *         precondition aka assumption
	 */
	int countChecks();

	String randomSeed();

	Optional<List> sample();

	Optional<List> originalSample();

	Optional<Throwable> throwable();

	GenerationMode generation();

	abstract class ResultBase implements PropertyCheckResult {

		protected final Status status;
		protected final String propertyName;
		protected final int tries;
		protected final int checks;
		protected final String randomSeed;
		protected final GenerationMode generation;

		ResultBase(Status status, String propertyName, int tries, int checks, String randomSeed, GenerationMode generation) {
			this.status = status;
			this.propertyName = propertyName;
			this.tries = tries;
			this.checks = checks;
			this.randomSeed = randomSeed;
			this.generation = generation;
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
		public String randomSeed() {
			return randomSeed;
		}

		@Override
		public Optional<List> sample() {
			return Optional.empty();
		}

		@Override
		public Optional<List> originalSample() {
			return Optional.empty();
		}

		@Override
		public Optional<Throwable> throwable() {
			return Optional.empty();
		}

		@Override
		public GenerationMode generation() {
			return generation;
		}
	}

	static PropertyCheckResult satisfied(String stereotype, String propertyName, int tries, int checks, String randomSeed, GenerationMode generation) {
		return new ResultBase(Status.SATISFIED, propertyName, tries, checks, randomSeed, generation) {
			@Override
			public String toString() {
				return String.format("%s [%s] satisfied", stereotype, propertyName);
			}
		};
	}

	static PropertyCheckResult failure(
		String stereotype, String propertyName, int tries, int checks, String randomSeed, GenerationMode generation,
		List<Object> sample, List<Object> originalSample, Throwable throwable
	) {
		Status status = isFalsified(throwable) ? Status.FALSIFIED : Status.ERRONEOUS;
		return new ResultBase(status, propertyName, tries, checks, randomSeed, generation) {
			@Override
			public Optional<List> sample() {
				return Optional.of(sample);
			}

			@Override
			public Optional<List> originalSample() {
				return Optional.of(originalSample);
			}

			@Override
			public String toString() {
				String sampleString = sample.isEmpty() ? "" : String.format(" with sample %s", JqwikStringSupport.displayString(sample));
				return String.format("%s [%s] falsified%s", stereotype, propertyName, sampleString);
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.ofNullable(throwable);
			}

		};
	}

	static boolean isFalsified(Throwable throwable) {
		return throwable == null || throwable instanceof AssertionError;
	}

	static PropertyCheckResult erroneous(
		String stereotype, String propertyName, int tries, int checks, String randomSeed, GenerationMode generation,
		List sample, List originalSample, Throwable throwable
	) {
		return new ResultBase(Status.ERRONEOUS, propertyName, tries, checks, randomSeed, generation) {
			@Override
			public Optional<List> sample() {
				return Optional.ofNullable(sample);
			}

			@Override
			public Optional<Throwable> throwable() {
				return Optional.of(throwable);
			}

			@Override
			public Optional<List> originalSample() {
				return Optional.ofNullable(originalSample);
			}

			@Override
			public String toString() {
				return String.format("%s [%s] erroneous with sample %s and exception [%s]", stereotype, propertyName, sample, throwable);
			}
		};
	}

	static PropertyCheckResult exhausted(String stereotype, String propertyName, int tries, int checks, String randomSeed, GenerationMode generation) {
		return new ResultBase(Status.EXHAUSTED, propertyName, tries, checks, randomSeed, generation) {
			@Override
			public String toString() {
				int rejections = tries - checks;
				return String.format("%s [%s] exhausted after [%d] tries and [%d] rejections", stereotype, propertyName, tries, rejections);
			}
		};
	}

}
