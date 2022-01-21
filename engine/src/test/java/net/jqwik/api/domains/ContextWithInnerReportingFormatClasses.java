package net.jqwik.api.domains;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;

class ContextWithInnerReportingFormatClasses extends DomainContextBase implements SampleReportingFormat {

	@Provide
	Arbitrary<Instant> instants() {
		return Arbitraries.just(Instant.now());
	}

	@Provide
	Arbitrary<Date> dates() {
		return Arbitraries.just(new Date());
	}

	@Provide
	Arbitrary<LocalTime> localTimes() {
		return Arbitraries.just(LocalTime.now());
	}

	@Provide
	Arbitrary<LocalDate> localDates() {
		return Arbitraries.just(LocalDate.now());
	}

	@Override
	public boolean appliesTo(Object value) {
		return value instanceof Date;
	}

	@Override
	public Object report(Object value) {
		return "Date()";
	}

	class InstantFormat implements SampleReportingFormat {

		@Override
		public boolean appliesTo(Object value) {
			return value instanceof Instant;
		}

		@Override
		public Object report(Object value) {
			return "Instant()";
		}
	}

	class LocalTimeFormat implements SampleReportingFormat {

		@Override
		public boolean appliesTo(Object value) {
			return value instanceof LocalTime;
		}

		@Override
		public Object report(Object value) {
			return "LocalTime()";
		}
	}

	private class ShouldNotBeUsedBecausePrivate implements SampleReportingFormat {
		@Override
		public boolean appliesTo(Object value) {
			return value instanceof LocalDate;
		}

		@Override
		public Object report(Object value) {
			return "LocalDate()";
		}
	}

}
