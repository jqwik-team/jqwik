package net.jqwik.time.api.dateTimes.localDateTime.constraint;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

@Group
public class ValidTypesWithOwnArbitrariesTests {

	@Group
	class Ranges {

		@Property
		void dateTimeRange(@ForAll("dateTimes") @DateTimeRange(min = "2013-05-25T01:32:21.113943", max = "2020-08-23T01:32:21.113943") LocalDateTime dateTime) {
			LocalDateTime min = LocalDateTime.of(2013, Month.MAY, 25, 1, 32, 21, 113943000);
			LocalDateTime max = LocalDateTime.of(2020, Month.AUGUST, 23, 1, 32, 21, 113943000);
			assertThat(dateTime).isBetween(min, max);
		}

		@Property
		void dateRange(@ForAll("dateTimes") @DateRange(min = "2013-05-25", max = "2020-08-23") LocalDateTime dateTime) {
			assertThat(dateTime.toLocalDate()).isAfterOrEqualTo(LocalDate.of(2013, Month.MAY, 25));
			assertThat(dateTime.toLocalDate()).isBeforeOrEqualTo(LocalDate.of(2020, Month.AUGUST, 23));
		}

		@Property
		void yearRange(@ForAll("dateTimes") @YearRange(min = 2014, max = 2019) LocalDateTime dateTime) {
			assertThat(dateTime.getYear()).isGreaterThanOrEqualTo(2014);
			assertThat(dateTime.getYear()).isLessThanOrEqualTo(2019);
		}

		@Property
		void monthRange(@ForAll("dateTimes") @MonthRange(min = Month.MARCH, max = Month.JULY) LocalDateTime dateTime) {
			assertThat(dateTime.getMonth()).isGreaterThanOrEqualTo(Month.MARCH);
			assertThat(dateTime.getMonth()).isLessThanOrEqualTo(Month.JULY);
		}

		@Property
		void dayOfMonthRange(@ForAll("dateTimes") @DayOfMonthRange(min = 15, max = 20) LocalDateTime dateTime) {
			assertThat(dateTime.getDayOfMonth()).isGreaterThanOrEqualTo(15);
			assertThat(dateTime.getDayOfMonth()).isLessThanOrEqualTo(20);
		}

		@Property
		void dayOfWeekRange(@ForAll("dateTimes") @DayOfWeekRange(min = DayOfWeek.TUESDAY, max = DayOfWeek.FRIDAY) LocalDateTime dateTime) {
			assertThat(dateTime.getDayOfWeek()).isBetween(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY);
		}

		@Provide
		Arbitrary<LocalDateTime> dateTimes() {
			return of(
				LocalDateTime.MIN,
				LocalDateTime.of(2013, Month.MAY, 24, 1, 32, 21, 113942000),
				LocalDateTime.of(2014, Month.FEBRUARY, 24, 2, 43, 59),
				LocalDateTime.of(2014, Month.FEBRUARY, 14, 2, 43, 59),
				LocalDateTime.of(2014, Month.FEBRUARY, 15, 2, 43, 59),
				LocalDateTime.of(2019, Month.JULY, 11, 4, 31, 48, 1),
				LocalDateTime.of(2019, Month.JULY, 20, 4, 31, 48, 1),
				LocalDateTime.of(2019, Month.JULY, 21, 4, 31, 48, 1),
				LocalDateTime.of(2020, Month.AUGUST, 23, 1, 32, 21, 113944000),
				LocalDateTime.of(2021, Month.APRIL, 12, 3, 32, 21, 113944000),
				LocalDateTime.of(2021, Month.APRIL, 17, 3, 32, 21, 113944000),
				LocalDateTime.MAX
			);
		}

	}

	@Group
	class Precisions {

		@Property
		void hours(@ForAll("dateTimes") @Precision(value = HOURS) LocalDateTime dateTime) {
			assertThat(dateTime.getMinute()).isEqualTo(0);
			assertThat(dateTime.getSecond()).isEqualTo(0);
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void minutes(@ForAll("dateTimes") @Precision(value = MINUTES) LocalDateTime dateTime) {
			assertThat(dateTime.getSecond()).isEqualTo(0);
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void seconds(@ForAll("dateTimes") @Precision(value = SECONDS) LocalDateTime dateTime) {
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void millis(@ForAll("dateTimes") @Precision(value = MILLIS) LocalDateTime dateTime) {
			assertThat(dateTime.getNano() % 1_000_000).isEqualTo(0);
		}

		@Property
		void micros(@ForAll("dateTimes") @Precision(value = MICROS) LocalDateTime dateTime) {
			assertThat(dateTime.getNano() % 1_000).isEqualTo(0);
		}

		@Property
		void nanos(@ForAll("dateTimes") @Precision(value = NANOS) LocalDateTime dateTime) {
			assertThat(dateTime).isNotNull();
		}

		@Provide
		Arbitrary<LocalDateTime> dateTimes() {
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
			);
		}

	}

}
