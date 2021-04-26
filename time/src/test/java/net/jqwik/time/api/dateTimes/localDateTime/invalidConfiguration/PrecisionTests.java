package net.jqwik.time.api.dateTimes.localDateTime.invalidConfiguration;

import java.time.*;
import java.time.temporal.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

@Group
public class PrecisionTests {

	@Provide
	Arbitrary<LocalDateTime> precisionNanoseconds() {
		return DateTimes.dateTimes().ofPrecision(NANOS);
	}

	@Group
	class Generally {

		@Property
		void ofPrecision(@ForAll ChronoUnit chronoUnit) {

			Assume.that(!chronoUnit.equals(NANOS));
			Assume.that(!chronoUnit.equals(MICROS));
			Assume.that(!chronoUnit.equals(MILLIS));
			Assume.that(!chronoUnit.equals(SECONDS));
			Assume.that(!chronoUnit.equals(MINUTES));
			Assume.that(!chronoUnit.equals(HOURS));

			assertThatThrownBy(
				() -> DateTimes.dateTimes().ofPrecision(chronoUnit)
			).isInstanceOf(IllegalArgumentException.class);

		}

	}

	@Group
	class Hours {

		@Property
		void precisionMaxSoonAfterMin(
			@ForAll("precisionNanoseconds") LocalDateTime min,
			@ForAll @IntRange(min = 1, max = 200) int nanos
		) {

			LocalDateTime max = min.plusNanos(nanos);

			Assume.that(min.getMinute() != 0 && min.getSecond() != 0 && min.getNano() != 0);
			Assume.that(min.getHour() == max.getHour());

			assertThatThrownBy(
				() -> DateTimes.dateTimes().between(min, max).ofPrecision(HOURS).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);

		}

		@Property
		void precisionMinTooLate(@ForAll("precisionMinTimeTooLateProvide") LocalTime time) {

			Assume.that(time.getMinute() != 0 || time.getSecond() != 0 || time.getNano() != 0);

			LocalDateTime min = LocalDateTime.of(LocalDate.MAX, time);
			LocalDateTime max = LocalDateTime.of(LocalDate.MAX, LocalTime.MAX);

			Assertions.setMaxStackTraceElementsDisplayed(Integer.MAX_VALUE);
			assertThatThrownBy(
				() -> DateTimes.dateTimes().between(min, max).ofPrecision(HOURS).generator(1000)
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
			@ForAll("precisionNanoseconds") LocalDateTime min,
			@ForAll @IntRange(min = 1, max = 200) int nanos
		) {

			LocalDateTime max = min.plusNanos(nanos);

			Assume.that(min.getSecond() != 0 && min.getNano() != 0);
			Assume.that(min.getMinute() == max.getMinute());

			assertThatThrownBy(
				() -> DateTimes.dateTimes().between(min, max).ofPrecision(MINUTES).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);

		}

		@Property
		void precisionMinTimeTooLate(@ForAll("precisionMinTimeTooLateProvide") LocalTime time) {

			Assume.that(time.getSecond() != 0 || time.getNano() != 0);

			LocalDateTime min = LocalDateTime.of(LocalDate.MAX, time);
			LocalDateTime max = LocalDateTime.of(LocalDate.MAX, LocalTime.MAX);

			assertThatThrownBy(
				() -> DateTimes.dateTimes().between(min, max).ofPrecision(MINUTES).generator(1000)
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
			@ForAll("precisionNanoseconds") LocalDateTime min,
			@ForAll @IntRange(min = 1, max = 200) int nanos
		) {

			LocalDateTime max = min.plusNanos(nanos);

			Assume.that(min.getNano() != 0);
			Assume.that(min.getSecond() == max.getSecond());

			assertThatThrownBy(
				() -> DateTimes.dateTimes().between(min, max).ofPrecision(SECONDS).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);

		}

		@Property
		void precisionMinTimeTooLate(
			@ForAll("precisionMinTimeTooLateProvide") LocalTime time,
			@ForAll @IntRange(min = 1, max = 999_999_999) int nanos
		) {

			time = time.withNano(nanos);
			LocalDateTime min = LocalDateTime.of(LocalDate.MAX, time);
			LocalDateTime max = LocalDateTime.of(LocalDate.MAX, LocalTime.MAX);

			assertThatThrownBy(
				() -> DateTimes.dateTimes().between(min, max).ofPrecision(SECONDS).generator(1000)
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
			@ForAll("precisionNanoseconds") LocalDateTime min,
			@ForAll @IntRange(min = 1, max = 200) int nanos
		) {

			LocalDateTime max = min.plusNanos(nanos);

			Assume.that(min.getNano() % 1_000_000 != 0);
			Assume.that(min.getNano() % 1_000_000 + nanos < 1_000_000);

			assertThatThrownBy(
				() -> DateTimes.dateTimes().between(min, max).ofPrecision(MILLIS).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);

		}

		@Property
		void precisionMinTimeTooLate(
			@ForAll("precisionMinTimeTooLateProvide") LocalTime time,
			@ForAll @IntRange(min = 999_000_001, max = 999_999_999) int nanos
		) {

			time = time.withNano(nanos);
			LocalDateTime min = LocalDateTime.of(LocalDate.MAX, time);
			LocalDateTime max = LocalDateTime.of(LocalDate.MAX, LocalTime.MAX);

			assertThatThrownBy(
				() -> DateTimes.dateTimes().between(min, max).ofPrecision(MILLIS).generator(1000)
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
			@ForAll("precisionNanoseconds") LocalDateTime min,
			@ForAll @IntRange(min = 1, max = 200) int nanos
		) {

			LocalDateTime max = min.plusNanos(nanos);

			Assume.that(min.getNano() % 1_000 != 0);
			Assume.that(min.getNano() % 1_000 + nanos < 1_000);

			assertThatThrownBy(
				() -> DateTimes.dateTimes().between(min, max).ofPrecision(MICROS).generator(1000)
			).isInstanceOf(IllegalArgumentException.class);

		}

		@Property
		void precisionMinTimeTooLate(
			@ForAll("precisionMinTimeTooLateProvide") LocalTime time,
			@ForAll @IntRange(min = 999_999_001, max = 999_999_999) int nanos
		) {

			time = time.withNano(nanos);
			LocalDateTime min = LocalDateTime.of(LocalDate.MAX, time);
			LocalDateTime max = LocalDateTime.of(LocalDate.MAX, LocalTime.MAX);

			assertThatThrownBy(
				() -> DateTimes.dateTimes().between(min, max).ofPrecision(MICROS).generator(1000)
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
