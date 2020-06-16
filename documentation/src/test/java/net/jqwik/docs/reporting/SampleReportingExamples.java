package net.jqwik.docs.reporting;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class SampleReportingExamples {

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
	void reportFalsifiedSamples(
		@ForAll int anInt,
		@ForAll List<Integer> listOfInts,
		@ForAll @Size(min = 3) Map<@AlphaChars @StringLength(3) String, Integer> aMap
	) {
		Assertions.assertThat(anInt).isLessThan(10);
	}

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
	void reportFalsifiedArrays(
		@ForAll int anInt,
		@ForAll int[] arrayOfInts,
		@ForAll @Size(min = 2) @AlphaChars @StringLength(3) String[] arrayOfStrings
	) {
		Assertions.assertThat(anInt).isLessThan(10);
	}

	@Property(tries = 10, afterFailure = AfterFailureMode.RANDOM_SEED)
	// @Report(Reporting.GENERATED)
	void reportMultilineToString(@ForAll @UseType Person person) {
		Assertions.assertThat(person.firstName).hasSizeLessThan(10);
	}

	static class Person {
		private final String firstName;
		private final String lastName;
		private final String cv;

		public Person(String firstName, String lastName, String cv) {
			if (cv.length() < 20) {
				throw new IllegalArgumentException();
			}
			this.firstName = firstName;
			this.lastName = lastName;
			this.cv = cv;
		}

		@Override
		public String toString() {
			return String.format(
				"Person{%n  firstName: '%s'%n  lastName: '%s'%n  cv: %s%n}",
				firstName,
				lastName,
				cv
			);
		}
	}
}
