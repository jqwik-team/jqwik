package net.jqwik.time.api.times.duration;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.constraints.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.Arbitraries.*;
import static net.jqwik.time.api.testingSupport.ForDuration.*;

@Group
public class ConstraintTests {

	@Group
	class Constraints {

		@Property
		void durationRangeMin(@ForAll @DurationRange(min = "PT-3000H-39M-22.123111444S") Duration duration) {
			Duration start = Duration.ofSeconds(-3000 * 60 * 60 - 39 * 60 - 22, -123111444);
			assertThat(duration.compareTo(start)).isGreaterThanOrEqualTo(0);
		}

		@Property
		void durationRangeMax(@ForAll @DurationRange(max = "PT1999H22M11S") Duration duration) {
			Duration end = Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 11);
			assertThat(duration.compareTo(end)).isLessThanOrEqualTo(0);
		}

		@Property
		void durationRangeDefaultNotAffectDefaultPrecision(@ForAll @DurationRange Duration duration) {
			assertThat(duration.getNano()).isEqualTo(0);
		}

		@Group
		class Precisions {

			@Property
			void hours(@ForAll @Precision(value = HOURS) Duration duration) {
				assertThat(getMinute(duration)).isZero();
				assertThat(getSecond(duration)).isZero();
				assertThat(duration.getNano()).isZero();
			}

			@Property
			void minutes(@ForAll @Precision(value = MINUTES) Duration duration) {
				assertThat(getSecond(duration)).isZero();
				assertThat(duration.getNano()).isZero();
			}

			@Property
			void seconds(@ForAll @Precision(value = SECONDS) Duration duration) {
				assertThat(duration.getNano()).isZero();
			}

			@Property
			void millis(@ForAll @Precision(value = MILLIS) Duration duration) {
				assertThat(duration.getNano() % 1_000_000).isZero();
			}

			@Property
			void micros(@ForAll @Precision(value = MICROS) Duration duration) {
				assertThat(duration.getNano() % 1_000).isZero();
			}

			@Property
			void nanos(@ForAll @Precision(value = NANOS) Duration duration) {
				assertThat(duration).isNotNull();
			}

		}

		@Group
		class InvalidConfiguration {

			@Group
			class DurationRangeConstraint {

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void minTooEarly(@ForAll @DurationRange(min = "PT-2562047788015215H-30M-8.000000001S") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void maxTooEarly(@ForAll @DurationRange(max = "PT-2562047788015215H-30M-8.000000001S") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void minTooLate(@ForAll @DurationRange(min = "PT2562047788015215H30M8S") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void maxTooLate(@ForAll @DurationRange(max = "PT2562047788015215H30M8S") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void minIllegalString(@ForAll @DurationRange(min = "foo") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void maxIllegalString(@ForAll @DurationRange(max = "foo") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void minWrongFormat(@ForAll @DurationRange(min = "2562047788015215H30M8S") Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = DateTimeException.class)
				void maxWrongFormat(@ForAll @DurationRange(max = "2562047788015215H30M8S") Duration duration) {
					//do nothing
				}

			}

			@Group
			class Precisions {

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void centuries(@ForAll @Precision(value = CENTURIES) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void days(@ForAll @Precision(value = DAYS) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void decades(@ForAll @Precision(value = DECADES) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void eras(@ForAll @Precision(value = ERAS) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void forever(@ForAll @Precision(value = FOREVER) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void halfDays(@ForAll @Precision(value = HALF_DAYS) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void millennia(@ForAll @Precision(value = MILLENNIA) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void months(@ForAll @Precision(value = MONTHS) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void weeks(@ForAll @Precision(value = WEEKS) Duration duration) {
					//do nothing
				}

				@Example
				@ExpectFailure(failureType = IllegalArgumentException.class)
				void years(@ForAll @Precision(value = YEARS) Duration duration) {
					//do nothing
				}

			}

		}

	}

	@Group
	class InvalidUseOfConstraints {

		@Property
		void durationRange(@ForAll @DurationRange(max = "PT1999H22M11S") String string) {
			assertThat(string).isNotNull();
		}

	}

	@Group
	class ValidTypesWithOwnArbitraries {

		@Group
		class Ranges {

			@Property
			void duration(@ForAll("durations") @DurationRange(min = "PT1999H22M8S", max = "PT1999H22M11S") Duration duration) {
				assertThat(duration)
					.isBetween(Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 8), Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 11));
			}

			@Provide
			Arbitrary<Duration> durations() {
				return of(
					Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 6),
					Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 7),
					Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 8),
					Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 9),
					Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 10),
					Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 11),
					Duration.ofSeconds(1999 * 60 * 60 + 22 * 60 + 12)
				);
			}

		}

		@Group
		class Precisions {

			@Property
			void hours(@ForAll("durations") @Precision(value = HOURS) Duration duration) {
				assertThat(getMinute(duration)).isEqualTo(0);
				assertThat(getSecond(duration)).isEqualTo(0);
				assertThat(duration.getNano()).isEqualTo(0);
			}

			@Property
			void minutes(@ForAll("durations") @Precision(value = MINUTES) Duration duration) {
				assertThat(getSecond(duration)).isEqualTo(0);
				assertThat(duration.getNano()).isEqualTo(0);
			}

			@Property
			void seconds(@ForAll("durations") @Precision(value = SECONDS) Duration duration) {
				assertThat(duration.getNano()).isEqualTo(0);
			}

			@Property
			void millis(@ForAll("durations") @Precision(value = MILLIS) Duration duration) {
				assertThat(duration.getNano() % 1_000_000).isEqualTo(0);
			}

			@Property
			void micros(@ForAll("durations") @Precision(value = MICROS) Duration duration) {
				assertThat(duration.getNano() % 1_000).isEqualTo(0);
			}

			@Property
			void nanos(@ForAll("durations") @Precision(value = NANOS) Duration duration) {
				assertThat(duration).isNotNull();
			}

			@Provide
			Arbitrary<Duration> durations() {
				return of(
					//For Hours
					Duration.ofSeconds(1999 * 60 * 60 + 0 * 60 + 0),
					Duration.ofSeconds(33 * 60 * 60 + 22 * 60 + 0),
					Duration.ofSeconds(1312 * 60 * 60 + 0 * 60 + 33),
					Duration.ofSeconds(31212 * 60 * 60 + 55 * 60 + 55, 222),
					Duration.ofSeconds(3432 * 60 * 60 + 0 * 60 + 0),
					Duration.ofSeconds(42332 * 60 * 60 + 3 * 60 + 0),
					Duration.ofSeconds(1211 * 60 * 60 + 11 * 60 + 11, 111_111_111),
					//For Minutes
					Duration.ofSeconds(1999 * 60 * 60 + 11 * 60 + 0),
					Duration.ofSeconds(33 * 60 * 60 + 22 * 60 + 0, 111_203),
					Duration.ofSeconds(1312 * 60 * 60 + 31 * 60 + 33),
					Duration.ofSeconds(31212 * 60 * 60 + 55 * 60 + 55, 222),
					Duration.ofSeconds(3432 * 60 * 60 + 12 * 60 + 0),
					Duration.ofSeconds(42332 * 60 * 60 + 3 * 60 + 0),
					Duration.ofSeconds(1211 * 60 * 60 + 11 * 60 + 11, 111_111_111),
					//For Seconds
					Duration.ofSeconds(1999 * 60 * 60 + 11 * 60 + 33, 2_301_302),
					Duration.ofSeconds(33 * 60 * 60 + 22 * 60 + 11, 111_203),
					Duration.ofSeconds(1312 * 60 * 60 + 31 * 60 + 44),
					Duration.ofSeconds(31212 * 60 * 60 + 55 * 60 + 55, 222),
					Duration.ofSeconds(3432 * 60 * 60 + 12 * 60 + 31),
					Duration.ofSeconds(42332 * 60 * 60 + 3 * 60 + 21),
					Duration.ofSeconds(1211 * 60 * 60 + 11 * 60 + 11, 111_111_111),
					//For Millis
					Duration.ofSeconds(1999 * 60 * 60 + 11 * 60 + 33, 2_301_302),
					Duration.ofSeconds(33 * 60 * 60 + 22 * 60 + 11, 111_203),
					Duration.ofSeconds(1312 * 60 * 60 + 31 * 60 + 44, 391_000_000),
					Duration.ofSeconds(31212 * 60 * 60 + 55 * 60 + 55, 222),
					Duration.ofSeconds(3432 * 60 * 60 + 12 * 60 + 31, 9_000_000),
					Duration.ofSeconds(42332 * 60 * 60 + 3 * 60 + 21, 103_000_000),
					Duration.ofSeconds(1211 * 60 * 60 + 11 * 60 + 11, 111_111_111),
					//For Micros
					Duration.ofSeconds(1999 * 60 * 60 + 11 * 60 + 33, 2_301_302),
					Duration.ofSeconds(33 * 60 * 60 + 22 * 60 + 11, 111_203),
					Duration.ofSeconds(1312 * 60 * 60 + 31 * 60 + 44, 391_312_000),
					Duration.ofSeconds(31212 * 60 * 60 + 55 * 60 + 55, 222),
					Duration.ofSeconds(3432 * 60 * 60 + 12 * 60 + 31, 9_324_000),
					Duration.ofSeconds(42332 * 60 * 60 + 3 * 60 + 21, 103_232_000),
					Duration.ofSeconds(1211 * 60 * 60 + 11 * 60 + 11, 111_111_111),
					//For Nanos
					Duration.ofSeconds(1999 * 60 * 60 + 11 * 60 + 33, 2_301_302),
					Duration.ofSeconds(33 * 60 * 60 + 22 * 60 + 11, 111_203),
					Duration.ofSeconds(1312 * 60 * 60 + 31 * 60 + 44, 391_312_312),
					Duration.ofSeconds(31212 * 60 * 60 + 55 * 60 + 55, 222),
					Duration.ofSeconds(3432 * 60 * 60 + 12 * 60 + 31, 9_324_422),
					Duration.ofSeconds(42332 * 60 * 60 + 3 * 60 + 21, 103_232_321),
					Duration.ofSeconds(1211 * 60 * 60 + 11 * 60 + 11, 111_111_111)
				);
			}

		}

	}

}
