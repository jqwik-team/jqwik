package net.jqwik.time.api.dateTimes.zonedDateTime.constraint;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

@Group
public class ValidTypesWithOwnArbitrariesTests {

	@Group
	class Ranges {

		@Property
		void dateTimeRange(@ForAll("dateTimes") @DateTimeRange(min = "2013-05-25T01:32:21.113943", max = "2020-08-23T01:32:21.113943") ZonedDateTime dateTime) {
			LocalDateTime min = LocalDateTime.of(2013, Month.MAY, 25, 1, 32, 21, 113943000);
			LocalDateTime max = LocalDateTime.of(2020, Month.AUGUST, 23, 1, 32, 21, 113943000);
			assertThat(dateTime.toLocalDateTime()).isBetween(min, max);
		}

		@Property
		void dateRange(@ForAll("dateTimes") @DateRange(min = "2013-05-25", max = "2020-08-23") ZonedDateTime dateTime) {
			assertThat(dateTime.toLocalDate()).isAfterOrEqualTo(LocalDate.of(2013, Month.MAY, 25));
			assertThat(dateTime.toLocalDate()).isBeforeOrEqualTo(LocalDate.of(2020, Month.AUGUST, 23));
		}

		@Property
		void yearRange(@ForAll("dateTimes") @YearRange(min = 2014, max = 2019) ZonedDateTime dateTime) {
			assertThat(dateTime.getYear()).isGreaterThanOrEqualTo(2014);
			assertThat(dateTime.getYear()).isLessThanOrEqualTo(2019);
		}

		@Property
		void monthRange(@ForAll("dateTimes") @MonthRange(min = Month.MARCH, max = Month.JULY) ZonedDateTime dateTime) {
			assertThat(dateTime.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(dateTime.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRange(@ForAll("dateTimes") @DayOfMonthRange(min = 15, max = 20) ZonedDateTime dateTime) {
			assertThat(dateTime.getDayOfMonth()).isGreaterThanOrEqualTo(15);
			assertThat(dateTime.getDayOfMonth()).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRange(@ForAll("dateTimes") @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) ZonedDateTime dateTime) {
			assertThat(dateTime.getDayOfWeek()).isBetween(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY);
		}

		@Property
		void timeRange(@ForAll("dateTimes") @TimeRange(min = "09:29:20.113943", max = "14:34:24.113943") ZonedDateTime dateTime) {
			assertThat(dateTime.toLocalTime()).isBetween(LocalTime.of(9, 29, 20, 113943000), LocalTime.of(14, 34, 24, 113943000));
		}

		@Property
		void hourRange(@ForAll("dateTimes") @HourRange(min = 11, max = 13) ZonedDateTime dateTime) {
			assertThat(dateTime.getHour()).isBetween(11, 13);
		}

		@Property
		void minuteRange(@ForAll("dateTimes") @MinuteRange(min = 31, max = 33) ZonedDateTime dateTime) {
			assertThat(dateTime.getMinute()).isBetween(31, 33);
		}

		@Property
		void secondRange(@ForAll("dateTimes") @SecondRange(min = 22, max = 25) ZonedDateTime dateTime) {
			assertThat(dateTime.getSecond()).isBetween(22, 25);
		}

		@Provide
		Arbitrary<ZonedDateTime> dateTimes() {
			Arbitrary<ZoneId> zoneIdArbitrary = Times.zoneIds();
			Arbitrary<LocalDateTime> localDateTimeArbitrary = of(
				LocalDateTime.of(2013, Month.MAY, 24, 9, 29, 20, 113942999),
				LocalDateTime.of(2014, Month.FEBRUARY, 24, 14, 34, 24, 113943001),
				LocalDateTime.of(2014, Month.FEBRUARY, 14, 14, 34, 24, 113943),
				LocalDateTime.of(2014, Month.FEBRUARY, 15, 10, 31, 59),
				LocalDateTime.of(2019, Month.JULY, 11, 11, 31, 21, 1),
				LocalDateTime.of(2019, Month.JULY, 20, 12, 32, 22, 1),
				LocalDateTime.of(2019, Month.JULY, 21, 13, 33, 23, 1),
				LocalDateTime.of(2020, Month.AUGUST, 23, 14, 34, 24, 113943000),
				LocalDateTime.of(2021, Month.APRIL, 12, 15, 31, 25, 113944000),
				LocalDateTime.of(2021, Month.APRIL, 17, 16, 30, 26, 113944000)
			);
			return Combinators.combine(localDateTimeArbitrary, zoneIdArbitrary).as(ZonedDateTime::of);
		}

	}

	@Group
	class Precisions {

		@Property
		void hours(@ForAll("dateTimes") @Precision(value = HOURS) ZonedDateTime dateTime) {
			assertThat(dateTime.getMinute()).isEqualTo(0);
			assertThat(dateTime.getSecond()).isEqualTo(0);
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void minutes(@ForAll("dateTimes") @Precision(value = MINUTES) ZonedDateTime dateTime) {
			assertThat(dateTime.getSecond()).isEqualTo(0);
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void seconds(@ForAll("dateTimes") @Precision(value = SECONDS) ZonedDateTime dateTime) {
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void millis(@ForAll("dateTimes") @Precision(value = MILLIS) ZonedDateTime dateTime) {
			assertThat(dateTime.getNano() % 1_000_000).isEqualTo(0);
		}

		@Property
		void micros(@ForAll("dateTimes") @Precision(value = MICROS) ZonedDateTime dateTime) {
			assertThat(dateTime.getNano() % 1_000).isEqualTo(0);
		}

		@Property
		void nanos(@ForAll("dateTimes") @Precision(value = NANOS) ZonedDateTime dateTime) {
			assertThat(dateTime).isNotNull();
		}

		@Provide
		Arbitrary<ZonedDateTime> dateTimes() {
			Arbitrary<ZoneId> zoneIdArbitrary = Times.zoneIds();
			Arbitrary<LocalDateTime> localDateTimeArbitrary = of(
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
			);
			return Combinators.combine(localDateTimeArbitrary, zoneIdArbitrary).as(ZonedDateTime::of);
		}

	}

}
