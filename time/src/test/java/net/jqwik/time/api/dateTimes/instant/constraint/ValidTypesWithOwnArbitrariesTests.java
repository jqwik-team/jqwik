package net.jqwik.time.api.dateTimes.instant.constraint;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

@Group
public class ValidTypesWithOwnArbitrariesTests {

	@Group
	class Ranges {

		@Property
		void instantRange(@ForAll("instants") @InstantRange(min = "2013-05-25T01:32:21.113943Z", max = "2020-08-23T01:32:21.113943Z") Instant instant) {
			LocalDateTime min = LocalDateTime.of(2013, Month.MAY, 25, 1, 32, 21, 113943000);
			LocalDateTime max = LocalDateTime.of(2020, Month.AUGUST, 23, 1, 32, 21, 113943000);
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant)).isBetween(min, max);
		}

		@Property
		void instantRangeNotAllowedInstantsHereAllowed(@ForAll("instantsNotAllowed") @InstantRange(min = "2013-05-25T01:32:21.113943Z", max = "2020-08-23T01:32:21.113943Z") Instant instant) {
			LocalDateTime min = LocalDateTime.of(2013, Month.MAY, 25, 1, 32, 21, 113943000);
			LocalDateTime max = LocalDateTime.of(2020, Month.AUGUST, 23, 1, 32, 21, 113943000);
			assertThat(DefaultInstantArbitrary.instantToLocalDateTime(instant)).isBetween(min, max);
		}

		@Property
		void dateRange(@ForAll("instants") @DateRange(min = "2013-05-25", max = "2020-08-23") Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.toLocalDate()).isAfterOrEqualTo(LocalDate.of(2013, Month.MAY, 25));
			assertThat(dateTime.toLocalDate()).isBeforeOrEqualTo(LocalDate.of(2020, Month.AUGUST, 23));
		}

		@Property
		void dateRangeNotAllowedInstants(@ForAll("instantsNotAllowed") @DateRange(min = "2013-05-25", max = "2020-08-23") Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.toLocalDate()).isAfterOrEqualTo(LocalDate.of(2013, Month.MAY, 25));
			assertThat(dateTime.toLocalDate()).isBeforeOrEqualTo(LocalDate.of(2020, Month.AUGUST, 23));
		}

		@Property
		void yearRange(@ForAll("instants") @YearRange(min = 2014, max = 2019) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getYear()).isGreaterThanOrEqualTo(2014);
			assertThat(dateTime.getYear()).isLessThanOrEqualTo(2019);
		}

		@Property
		void yearRangeNotAllowedInstants(@ForAll("instantsNotAllowed") @YearRange(min = 2014, max = 2019) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getYear()).isGreaterThanOrEqualTo(2014);
			assertThat(dateTime.getYear()).isLessThanOrEqualTo(2019);
		}

		@Property
		void monthRange(@ForAll("instants") @MonthRange(min = Month.MARCH, max = Month.JULY) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(dateTime.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void monthRangeNotAllowedInstants(@ForAll("instantsNotAllowed") @MonthRange(min = Month.MARCH, max = Month.JULY) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(dateTime.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRange(@ForAll("instants") @DayOfMonthRange(min = 15, max = 20) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getDayOfMonth()).isGreaterThanOrEqualTo(15);
			assertThat(dateTime.getDayOfMonth()).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfMonthRangeNotAllowedInstants(@ForAll("instantsNotAllowed") @DayOfMonthRange(min = 15, max = 20) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getDayOfMonth()).isGreaterThanOrEqualTo(15);
			assertThat(dateTime.getDayOfMonth()).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRange(@ForAll("instants") @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getDayOfWeek()).isBetween(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY);
		}

		@Property
		void dayOfWeekRangeNotAllowedInstants(@ForAll("instantsNotAllowed") @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getDayOfWeek()).isBetween(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY);
		}

		@Property
		void timeRange(@ForAll("instants") @TimeRange(min = "09:29:20.113943", max = "14:34:24.113943") Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.toLocalTime()).isBetween(LocalTime.of(9, 29, 20, 113943000), LocalTime.of(14, 34, 24, 113943000));
		}

		@Property
		void timeRangeNotAllowedInstants(@ForAll("instantsNotAllowed") @TimeRange(min = "09:29:20.113943", max = "14:34:24.113943") Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.toLocalTime()).isBetween(LocalTime.of(9, 29, 20, 113943000), LocalTime.of(14, 34, 24, 113943000));
		}

		@Property
		void hourRange(@ForAll("instants") @HourRange(min = 11, max = 13) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getHour()).isBetween(11, 13);
		}

		@Property
		void hourRangeNotAllowedInstants(@ForAll("instantsNotAllowed") @HourRange(min = 11, max = 13) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getHour()).isBetween(11, 13);
		}

		@Property
		void minuteRange(@ForAll("instants") @MinuteRange(min = 31, max = 33) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getMinute()).isBetween(31, 33);
		}

		@Property
		void minuteRangeNotAllowedInstants(@ForAll("instantsNotAllowed") @MinuteRange(min = 31, max = 33) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getMinute()).isBetween(31, 33);
		}

		@Property
		void secondRange(@ForAll("instants") @SecondRange(min = 22, max = 25) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getSecond()).isBetween(22, 25);
		}

		@Property
		void secondRangeNotAllowedInstants(@ForAll("instantsNotAllowed") @SecondRange(min = 22, max = 25) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getSecond()).isBetween(22, 25);
		}

		@Provide
		Arbitrary<Instant> instants() {
			return of(
				LocalDateTime.MIN,
				LocalDateTime.of(2013, Month.MAY, 24, 9, 29, 20, 113942999),
				LocalDateTime.of(2014, Month.FEBRUARY, 24, 14, 34, 24, 113943001),
				LocalDateTime.of(2014, Month.FEBRUARY, 14, 14, 34, 24, 113943),
				LocalDateTime.of(2014, Month.FEBRUARY, 15, 10, 31, 59),
				LocalDateTime.of(2019, Month.JULY, 11, 11, 31, 21, 1),
				LocalDateTime.of(2019, Month.JULY, 20, 12, 32, 22, 1),
				LocalDateTime.of(2019, Month.JULY, 21, 13, 33, 23, 1),
				LocalDateTime.of(2020, Month.AUGUST, 23, 14, 34, 24, 113943000),
				LocalDateTime.of(2021, Month.APRIL, 12, 15, 31, 25, 113944000),
				LocalDateTime.of(2021, Month.APRIL, 17, 16, 30, 26, 113944000),
				LocalDateTime.MAX
			).map(v -> v.toInstant(ZoneOffset.UTC));
		}

	}

	@Group
	class Precisions {

		@Property
		void hours(@ForAll("instants") @Precision(value = HOURS) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getMinute()).isEqualTo(0);
			assertThat(dateTime.getSecond()).isEqualTo(0);
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void minutes(@ForAll("instants") @Precision(value = MINUTES) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getSecond()).isEqualTo(0);
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void seconds(@ForAll("instants") @Precision(value = SECONDS) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void millis(@ForAll("instants") @Precision(value = MILLIS) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getNano() % 1_000_000).isEqualTo(0);
		}

		@Property
		void micros(@ForAll("instants") @Precision(value = MICROS) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime.getNano() % 1_000).isEqualTo(0);
		}

		@Property
		void nanos(@ForAll("instants") @Precision(value = NANOS) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime).isNotNull();
		}

		@Property
		void nanosNotAllowedInstants(@ForAll("instantsNotAllowed") @Precision(value = NANOS) Instant instant) {
			LocalDateTime dateTime = DefaultInstantArbitrary.instantToLocalDateTime(instant);
			assertThat(dateTime).isNotNull();
		}

		@Provide
		Arbitrary<Instant> instants() {
			return of(
				//For Hours
				LocalDateTime.of(2021, 3, 15, 9, 0, 3),
				LocalDateTime.of(1997, 12, 17, 10, 3, 0),
				LocalDateTime.of(2020, 8, 23, 11, 0, 0),
				LocalDateTime.of(2013, 5, 25, 12, 0, 0, 312),
				LocalDateTime.of(1995, 3, 12, 13, 0, 0, 392_291_392),
				LocalDateTime.of(2400, 2, 2, 14, 0, 0),
				LocalDateTime.of(8473929, 3, 1, 15, 1, 1, 111_111_111),
				//For Minutes
				LocalDateTime.of(2021, 3, 15, 9, 12, 3),
				LocalDateTime.of(1997, 12, 17, 10, 3, 0),
				LocalDateTime.of(2020, 8, 23, 11, 13, 0, 333_211),
				LocalDateTime.of(2013, 5, 25, 12, 14, 0, 312),
				LocalDateTime.of(1995, 3, 12, 13, 13, 0, 392_291_392),
				LocalDateTime.of(2400, 2, 2, 14, 44, 0),
				LocalDateTime.of(8473929, 3, 1, 15, 1, 1, 111_111_111),
				//For Seconds
				LocalDateTime.of(2021, 3, 15, 9, 12, 3),
				LocalDateTime.of(1997, 12, 17, 10, 3, 31),
				LocalDateTime.of(2020, 8, 23, 11, 13, 32, 333_211),
				LocalDateTime.of(2013, 5, 25, 12, 14, 11, 312),
				LocalDateTime.of(1995, 3, 12, 13, 13, 33, 392_291_392),
				LocalDateTime.of(2400, 2, 2, 14, 44, 14),
				LocalDateTime.of(8473929, 3, 1, 15, 1, 1, 111_111_111),
				//For Millis
				LocalDateTime.of(2021, 3, 15, 9, 12, 3, 322_000_000),
				LocalDateTime.of(1997, 12, 17, 10, 3, 31, 321_000_000),
				LocalDateTime.of(2020, 8, 23, 11, 13, 32, 333_211),
				LocalDateTime.of(2013, 5, 25, 12, 14, 11, 312),
				LocalDateTime.of(1995, 3, 12, 13, 13, 33, 392_291_392),
				LocalDateTime.of(2400, 2, 2, 14, 44, 14, 312_000_000),
				LocalDateTime.of(8473929, 3, 1, 15, 1, 1, 111_111_111),
				//For Micros
				LocalDateTime.of(2021, 3, 15, 9, 12, 3, 322_212_000),
				LocalDateTime.of(1997, 12, 17, 10, 3, 31, 321_312_000),
				LocalDateTime.of(2020, 8, 23, 11, 13, 32, 333_211),
				LocalDateTime.of(2013, 5, 25, 12, 14, 11, 312),
				LocalDateTime.of(1995, 3, 12, 13, 13, 33, 392_291_392),
				LocalDateTime.of(2400, 2, 2, 14, 44, 14, 312_344_000),
				LocalDateTime.of(8473929, 3, 1, 15, 1, 1, 111_111_111),
				//For Nanos
				LocalDateTime.of(2021, 3, 15, 9, 12, 3, 322_212_333),
				LocalDateTime.of(1997, 12, 17, 10, 3, 31, 321_312_111),
				LocalDateTime.of(2020, 8, 23, 11, 13, 32, 333_211),
				LocalDateTime.of(2013, 5, 25, 12, 14, 11, 312),
				LocalDateTime.of(1995, 3, 12, 13, 13, 33, 392_291_392),
				LocalDateTime.of(2400, 2, 2, 14, 44, 14, 312_344_000),
				LocalDateTime.of(8473929, 3, 1, 15, 1, 1, 111_111_111)
			).map(v -> v.toInstant(ZoneOffset.UTC));
		}

	}

	@Provide
	Arbitrary<Instant> instantsNotAllowed() {
		return of(
			Instant.MIN,
			Instant.MAX,
			LocalDateTime.of(2019, Month.JULY, 20, 12, 32, 22, 1).toInstant(ZoneOffset.UTC),
			LocalDateTime.of(2021, Month.AUGUST, 4, 8, 21, 11, 349).toInstant(ZoneOffset.UTC)
		);
	}

}
