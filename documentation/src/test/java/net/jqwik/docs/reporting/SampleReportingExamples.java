package net.jqwik.docs.reporting;

import java.time.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;

public class SampleReportingExamples {

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

	@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
	@Report(Reporting.GENERATED)
	void reportWithFormat(@ForAll("dates") LocalDate localDate) {
		Assertions.assertThat(localDate).isBefore(LocalDate.of(2000, 1, 1));
	}

	@Provide
	Arbitrary<LocalDate> dates() {
		Arbitrary<Integer> years = Arbitraries.integers().between(1900, 2100);
		Arbitrary<Integer> months = Arbitraries.integers().between(1, 12);
		Arbitrary<Integer> days = Arbitraries.integers().between(1, 28);

		return Combinators.combine(years, months, days).as(LocalDate::of);
	}

	public static class LocalDateFormat implements SampleReportingFormat {

		@Override
		public boolean appliesTo(final Object value) {
			return value instanceof LocalDate;
		}

		@Override
		public Object report(final Object value) {
			LocalDate date = (LocalDate) value;
			Map<String, Object> valueMap = new HashMap<>();
			valueMap.put("year", date.getYear());
			valueMap.put("month", date.getMonth());
			valueMap.put("day", date.getDayOfMonth());
			return valueMap;
		}

		@Override
		public Optional<String> label(final Object value) {
			return Optional.of("LocalDate ");
		}
	}
}
