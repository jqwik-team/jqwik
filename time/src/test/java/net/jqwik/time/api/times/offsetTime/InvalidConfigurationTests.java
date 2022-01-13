package net.jqwik.time.api.times.offsetTime;

import java.time.*;
import java.time.temporal.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

@Group
@PropertyDefaults(tries = 100)
public class InvalidConfigurationTests {

	@Provide
	Arbitrary<OffsetTime> precisionNanoseconds() {
		return Times.offsetTimes().ofPrecision(NANOS);
	}

	@Group
	class InvalidValues {

		@Property
		void minMaxSecond(@ForAll int minSecond, @ForAll int maxSecond) {

			Assume.that(minSecond < 0 || minSecond > 59 || maxSecond < 0 || maxSecond > 59);

			assertThatThrownBy(
				() -> Times.offsetTimes().hourBetween(minSecond, maxSecond)
			).isInstanceOf(IllegalArgumentException.class);

		}

		@Property
		void minMaxMinute(@ForAll int minMinute, @ForAll int maxMinute) {

			Assume.that(minMinute < 0 || minMinute > 59 || maxMinute < 0 || maxMinute > 59);

			assertThatThrownBy(
				() -> Times.offsetTimes().hourBetween(minMinute, maxMinute)
			).isInstanceOf(IllegalArgumentException.class);

		}

		@Property
		void minMaxHour(@ForAll int minHour, @ForAll int maxHour) {

			Assume.that(minHour < 0 || minHour > 23 || maxHour < 0 || maxHour > 23);

			assertThatThrownBy(
				() -> Times.offsetTimes().hourBetween(minHour, maxHour)
			).isInstanceOf(IllegalArgumentException.class);

		}

	}

	@Group
	class TimeGenerationPrecision {

		@Group
		class Generally {

			@Property
			void minMaxHour(@ForAll ChronoUnit chronoUnit) {

				Assume.that(!chronoUnit.equals(NANOS));
				Assume.that(!chronoUnit.equals(MICROS));
				Assume.that(!chronoUnit.equals(MILLIS));
				Assume.that(!chronoUnit.equals(SECONDS));
				Assume.that(!chronoUnit.equals(MINUTES));
				Assume.that(!chronoUnit.equals(HOURS));

				assertThatThrownBy(
					() -> Times.offsetTimes().ofPrecision(chronoUnit)
				).isInstanceOf(IllegalArgumentException.class);

			}

		}

		@Group
		class Hours {

			@Property
			void precisionMaxTimeSoonAfterMinTime(
				@ForAll("precisionNanoseconds") OffsetTime offsetTime,
				@ForAll @IntRange(min = 1, max = 200) int nanos
			) {

				LocalTime startTime = offsetTime.toLocalTime();
				LocalTime endTime = startTime.plusNanos(nanos);

				Assume.that(endTime.isAfter(startTime));
				Assume.that(startTime.getMinute() != 0 && startTime.getSecond() != 0 && startTime.getNano() != 0);
				Assume.that(startTime.getHour() == endTime.getHour());

				assertThatThrownBy(
					() -> Times.offsetTimes().between(startTime, endTime).ofPrecision(HOURS).generator(1000)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Property
			void precisionMinTimeTooLate(@ForAll("precisionMinTimeTooLateProvide") LocalTime time) {

				Assume.that(time.getMinute() != 0 || time.getSecond() != 0 || time.getNano() != 0);

				assertThatThrownBy(
					() -> Times.offsetTimes().atTheEarliest(time).ofPrecision(HOURS).generator(1000)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Provide
			Arbitrary<LocalTime> precisionMinTimeTooLateProvide() {
				return Times.times().hourBetween(23, 23);
			}

		}

		@Group
		class Minutes {

			@Property
			void precisionMaxTimeSoonAfterMinTime(
				@ForAll("precisionNanoseconds") OffsetTime offsetTime,
				@ForAll @IntRange(min = 1, max = 200) int nanos
			) {

				LocalTime startTime = offsetTime.toLocalTime();
				LocalTime endTime = startTime.plusNanos(nanos);

				Assume.that(endTime.isAfter(startTime));
				Assume.that(startTime.getSecond() != 0 && startTime.getNano() != 0);
				Assume.that(startTime.getMinute() == endTime.getMinute());

				assertThatThrownBy(
					() -> Times.offsetTimes().between(startTime, endTime).ofPrecision(MINUTES).generator(1000)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Property
			void precisionMinTimeTooLate(@ForAll("precisionMinTimeTooLateProvide") LocalTime time) {

				Assume.that(time.getSecond() != 0 || time.getNano() != 0);

				assertThatThrownBy(
					() -> Times.offsetTimes().atTheEarliest(time).ofPrecision(MINUTES).generator(1000)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Provide
			Arbitrary<LocalTime> precisionMinTimeTooLateProvide() {
				return Times.times().hourBetween(23, 23).minuteBetween(59, 59);
			}

		}

		@Group
		class Seconds {

			@Property
			void precisionMaxTimeSoonAfterMinTime(
				@ForAll("precisionNanoseconds") OffsetTime offsetTime,
				@ForAll @IntRange(min = 1, max = 200) int nanos
			) {

				LocalTime startTime = offsetTime.toLocalTime();
				LocalTime endTime = startTime.plusNanos(nanos);

				Assume.that(endTime.isAfter(startTime));
				Assume.that(startTime.getNano() != 0);
				Assume.that(startTime.getSecond() == endTime.getSecond());

				assertThatThrownBy(
					() -> Times.offsetTimes().between(startTime, endTime).ofPrecision(SECONDS).generator(1000)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Property
			void precisionMinTimeTooLate(
				@ForAll("precisionMinTimeTooLateProvide") LocalTime time,
				@ForAll @IntRange(min = 1, max = 999_999_999) int nanos
			) {

				time = time.withNano(nanos);
				final LocalTime finalTime = time;

				assertThatThrownBy(
					() -> Times.offsetTimes().atTheEarliest(finalTime).ofPrecision(SECONDS).generator(1000)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Provide
			Arbitrary<LocalTime> precisionMinTimeTooLateProvide() {
				return Times.times().hourBetween(23, 23).minuteBetween(59, 59).secondBetween(59, 59);
			}

		}

		@Group
		class Millis {

			@Property
			void precisionMaxTimeSoonAfterMinTime(
				@ForAll("precisionNanoseconds") OffsetTime offsetTime,
				@ForAll @IntRange(min = 1, max = 200) int nanos
			) {

				LocalTime startTime = offsetTime.toLocalTime();
				LocalTime endTime = startTime.plusNanos(nanos);

				Assume.that(endTime.isAfter(startTime));
				Assume.that(startTime.getNano() % 1_000_000 != 0);
				Assume.that(startTime.getNano() % 1_000_000 + nanos < 1_000_000);

				assertThatThrownBy(
					() -> Times.offsetTimes().between(startTime, endTime).ofPrecision(MILLIS).generator(1000)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Property
			void precisionMinTimeTooLate(
				@ForAll("precisionMinTimeTooLateProvide") LocalTime time,
				@ForAll @IntRange(min = 999_000_001, max = 999_999_999) int nanos
			) {

				time = time.withNano(nanos);
				final LocalTime finalTime = time;

				assertThatThrownBy(
					() -> Times.offsetTimes().atTheEarliest(finalTime).ofPrecision(MILLIS).generator(1000)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Provide
			Arbitrary<LocalTime> precisionMinTimeTooLateProvide() {
				return Times.times().hourBetween(23, 23).minuteBetween(59, 59).secondBetween(59, 59);
			}

		}

		@Group
		class Micros {

			@Property
			void precisionMaxTimeSoonAfterMinTime(
				@ForAll("precisionNanoseconds") OffsetTime offsetTime,
				@ForAll @IntRange(min = 1, max = 200) int nanos
			) {

				LocalTime startTime = offsetTime.toLocalTime();
				LocalTime endTime = startTime.plusNanos(nanos);

				Assume.that(endTime.isAfter(startTime));
				Assume.that(startTime.getNano() % 1_000 != 0);
				Assume.that(startTime.getNano() % 1_000 + nanos < 1_000);

				assertThatThrownBy(
					() -> Times.offsetTimes().between(startTime, endTime).ofPrecision(MICROS).generator(1000)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Property
			void precisionMinTimeTooLate(
				@ForAll("precisionMinTimeTooLateProvide") LocalTime time,
				@ForAll @IntRange(min = 999_999_001, max = 999_999_999) int nanos
			) {

				time = time.withNano(nanos);
				final LocalTime finalTime = time;

				assertThatThrownBy(
					() -> Times.offsetTimes().atTheEarliest(finalTime).ofPrecision(MICROS).generator(1000)
				).isInstanceOf(IllegalArgumentException.class);

			}

			@Provide
			Arbitrary<LocalTime> precisionMinTimeTooLateProvide() {
				return Times.times().hourBetween(23, 23).minuteBetween(59, 59).secondBetween(59, 59);
			}

		}

		@Group
		class Nanos {

		}

	}

}
