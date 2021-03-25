package net.jqwik.time.api;

import java.time.*;
import java.time.format.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;

@Group
public class DateTimesConstraintTests {

	@Group
	class LocalDateTimeConstraints {

		@Property
		void dateTimeRangeMin(@ForAll @DateTimeRange(min = "2013-05-25T01:32:21.113943") LocalDateTime dateTime) {
			assertThat(dateTime).isAfterOrEqualTo(LocalDateTime.of(2013, Month.MAY, 25, 1, 32, 21, 113943000));
		}

		@Property
		void dateTimeRangeMax(@ForAll @DateTimeRange(max = "2020-08-23T01:32:21.113943") LocalDateTime dateTime) {
			assertThat(dateTime).isBeforeOrEqualTo(LocalDateTime.of(2020, Month.AUGUST, 23, 1, 32, 21, 113943000));
		}

		@Property
		void dateTimeRangeDefaultNotAffectDefaultPrecision(@ForAll @DateTimeRange LocalDateTime dateTime) {
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Group
		class Precisions {

			@Property
			void hours(@ForAll @Precision(value = HOURS) LocalDateTime dateTime) {
				assertThat(dateTime.getMinute()).isZero();
				assertThat(dateTime.getSecond()).isZero();
				assertThat(dateTime.getNano()).isZero();
			}

			@Property
			void minutes(@ForAll @Precision(value = MINUTES) LocalDateTime dateTime) {
				assertThat(dateTime.getSecond()).isZero();
				assertThat(dateTime.getNano()).isZero();
			}

			@Property
			void seconds(@ForAll @Precision(value = SECONDS) LocalDateTime dateTime) {
				assertThat(dateTime.getNano()).isZero();
			}

			@Property
			void millis(@ForAll @Precision(value = MILLIS) LocalDateTime dateTime) {
				assertThat(dateTime.getNano() % 1_000_000).isZero();
			}

			@Property
			void micros(@ForAll @Precision(value = MICROS) LocalDateTime dateTime) {
				assertThat(dateTime.getNano() % 1_000).isZero();
			}

			@Property
			void nanos(@ForAll @Precision(value = NANOS) LocalDateTime dateTime) {
				assertThat(dateTime).isNotNull();
			}

		}

		@Group
		class InvalidConfigurations {

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void minThrowsExceptionOnlyTime(@ForAll @DateTimeRange(min = "03:43:21") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void maxThrowsExceptionOnlyTime(@ForAll @DateTimeRange(max = "03:43:21") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void minThrowsExceptionOnlyTimeWithT(@ForAll @DateTimeRange(min = "T03:43:21") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void maxThrowsExceptionOnlyTimeWithT(@ForAll @DateTimeRange(max = "T03:43:21") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void minThrowsExceptionOnlyDate(@ForAll @DateTimeRange(min = "2013-05-25") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void maxThrowsExceptionOnlyDate(@ForAll @DateTimeRange(max = "2013-05-25") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void minThrowsExceptionYearMonth(@ForAll @DateTimeRange(min = "2013-05") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void maxThrowsExceptionYearMonth(@ForAll @DateTimeRange(max = "2013-05") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void minThrowsExceptionIllegalString(@ForAll @DateTimeRange(min = "foo") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void maxThrowsExceptionIllegalString(@ForAll @DateTimeRange(max = "foo") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void minThrowsExceptionMonthDay(@ForAll @DateTimeRange(min = "--05-25") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void maxThrowsExceptionMonthDay(@ForAll @DateTimeRange(max = "--05-25") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void minThrowsExceptionDay(@ForAll @DateTimeRange(min = "13") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void maxThrowsExceptionDay(@ForAll @DateTimeRange(max = "13") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void minThrowsExceptionPicoseconds(@ForAll @DateTimeRange(min = "2020-08-23T01:32:21.1139432111") LocalDateTime dateTime) {
				//do nothing
			}

			@Example
			@ExpectFailure(failureType = DateTimeParseException.class)
			void maxThrowsExceptionPicoseconds(@ForAll @DateTimeRange(max = "2020-08-23T01:32:21.1139432111") LocalDateTime dateTime) {
				//do nothing
			}

		}

	}

	@Group
	class InvalidUseOfConstraints {

		@Property
		void dateTimeRange(@ForAll @DateTimeRange(min = "2013-05-25T01:32:21.113943", max = "2020-08-23T01:32:21.113943") String string) {
			assertThat(string).isNotNull();
		}

		@Property
		void precision(@ForAll @Precision(value = HOURS) Byte b) {
			assertThat(b).isNotNull();
		}

	}

	@Group
	class ValidTypesWithOwnArbitraries {

		@Group
		class DateTimeRangeConstraint {

			@Property
			void localDateTime(@ForAll("localDateTimes") @DateTimeRange(min = "2013-05-25T01:32:21.113943", max = "2020-08-23T01:32:21.113943") LocalDateTime dateTime) {
				LocalDateTime min = LocalDateTime.of(2013, Month.MAY, 25, 1, 32, 21, 113943000);
				LocalDateTime max = LocalDateTime.of(2020, Month.AUGUST, 23, 1, 32, 21, 113943000);
				assertThat(dateTime).isBetween(min, max);
			}

			@Provide
			Arbitrary<LocalDateTime> localDateTimes() {
				return of(
						LocalDateTime.MIN,
						LocalDateTime.of(2013, Month.MAY, 25, 1, 32, 21, 113942000),
						LocalDateTime.of(2017, Month.FEBRUARY, 24, 2, 43, 59),
						LocalDateTime.of(2019, Month.JULY, 11, 4, 31, 48, 1),
						LocalDateTime.of(2020, Month.AUGUST, 23, 1, 32, 21, 113944000),
						LocalDateTime.MAX
				);
			}

		}

		@Group
		class PrecisionConstraint {

			@Group
			class Hours {

				@Property
				void localDateTime(@ForAll("dateTimes") @Precision(value = HOURS) LocalDateTime dateTime) {
					assertThat(dateTime.getMinute()).isEqualTo(0);
					assertThat(dateTime.getSecond()).isEqualTo(0);
					assertThat(dateTime.getNano()).isEqualTo(0);
				}

				@Provide
				Arbitrary<LocalDateTime> dateTimes() {
					return of(
							LocalDateTime.of(2021, 3, 15, 9, 0, 3),
							LocalDateTime.of(1997, 12, 17, 10, 3, 0),
							LocalDateTime.of(2020, 8, 23, 11, 0, 0),
							LocalDateTime.of(2013, 5, 25, 12, 0, 0, 312),
							LocalDateTime.of(1995, 3, 12, 13, 0, 0, 392_291_392),
							LocalDateTime.of(2400, 2, 2, 14, 0, 0),
							LocalDateTime.of(8473929, 3, 1, 15, 1, 1, 111_111_111)
					);
				}

			}

			@Group
			class Minutes {

				@Property
				void localTime(@ForAll("dateTimes") @Precision(value = MINUTES) LocalDateTime dateTime) {
					assertThat(dateTime.getSecond()).isEqualTo(0);
					assertThat(dateTime.getNano()).isEqualTo(0);
				}

				@Provide
				Arbitrary<LocalDateTime> dateTimes() {
					return of(
							LocalDateTime.of(2021, 3, 15, 9, 12, 3),
							LocalDateTime.of(1997, 12, 17, 10, 3, 0),
							LocalDateTime.of(2020, 8, 23, 11, 13, 0, 333_211),
							LocalDateTime.of(2013, 5, 25, 12, 14, 0, 312),
							LocalDateTime.of(1995, 3, 12, 13, 13, 0, 392_291_392),
							LocalDateTime.of(2400, 2, 2, 14, 44, 0),
							LocalDateTime.of(8473929, 3, 1, 15, 1, 1, 111_111_111)
					);
				}

			}

			@Group
			class Seconds {

				@Property
				void localTime(@ForAll("dateTimes") @Precision(value = SECONDS) LocalDateTime dateTime) {
					assertThat(dateTime.getNano()).isEqualTo(0);
				}

				@Provide
				Arbitrary<LocalDateTime> dateTimes() {
					return of(
							LocalDateTime.of(2021, 3, 15, 9, 12, 3),
							LocalDateTime.of(1997, 12, 17, 10, 3, 31),
							LocalDateTime.of(2020, 8, 23, 11, 13, 32, 333_211),
							LocalDateTime.of(2013, 5, 25, 12, 14, 11, 312),
							LocalDateTime.of(1995, 3, 12, 13, 13, 33, 392_291_392),
							LocalDateTime.of(2400, 2, 2, 14, 44, 14),
							LocalDateTime.of(8473929, 3, 1, 15, 1, 1, 111_111_111)
					);
				}

			}

			@Group
			class Millis {

				@Property
				void localTime(@ForAll("dateTimes") @Precision(value = MILLIS) LocalDateTime dateTime) {
					assertThat(dateTime.getNano() % 1_000_000).isEqualTo(0);
				}

				@Provide
				Arbitrary<LocalDateTime> dateTimes() {
					return of(
							LocalDateTime.of(2021, 3, 15, 9, 12, 3, 322_000_000),
							LocalDateTime.of(1997, 12, 17, 10, 3, 31, 321_000_000),
							LocalDateTime.of(2020, 8, 23, 11, 13, 32, 333_211),
							LocalDateTime.of(2013, 5, 25, 12, 14, 11, 312),
							LocalDateTime.of(1995, 3, 12, 13, 13, 33, 392_291_392),
							LocalDateTime.of(2400, 2, 2, 14, 44, 14, 312_000_000),
							LocalDateTime.of(8473929, 3, 1, 15, 1, 1, 111_111_111)
					);
				}

			}

			@Group
			class Micros {

				@Property
				void localTime(@ForAll("dateTimes") @Precision(value = MICROS) LocalDateTime dateTime) {
					assertThat(dateTime.getNano() % 1_000).isEqualTo(0);
				}

				@Provide
				Arbitrary<LocalDateTime> dateTimes() {
					return of(
							LocalDateTime.of(2021, 3, 15, 9, 12, 3, 322_212_000),
							LocalDateTime.of(1997, 12, 17, 10, 3, 31, 321_312_000),
							LocalDateTime.of(2020, 8, 23, 11, 13, 32, 333_211),
							LocalDateTime.of(2013, 5, 25, 12, 14, 11, 312),
							LocalDateTime.of(1995, 3, 12, 13, 13, 33, 392_291_392),
							LocalDateTime.of(2400, 2, 2, 14, 44, 14, 312_344_000),
							LocalDateTime.of(8473929, 3, 1, 15, 1, 1, 111_111_111)
					);
				}

			}

			@Group
			class Nanos {

				@Property
				void localTime(@ForAll("dateTimes") @Precision(value = NANOS) LocalDateTime dateTime) {
					assertThat(dateTime).isNotNull();
				}

				@Provide
				Arbitrary<LocalDateTime> dateTimes() {
					return of(
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

	}

}
