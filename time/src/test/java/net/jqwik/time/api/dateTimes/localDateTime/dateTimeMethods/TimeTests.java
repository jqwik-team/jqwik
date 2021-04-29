package net.jqwik.time.api.dateTimes.localDateTime.dateTimeMethods;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

public class TimeTests {

	@Provide
	Arbitrary<LocalDateTime> dateTimes() {
		return DateTimes.dateTimes();
	}

	@Provide
	Arbitrary<LocalDateTime> precisionHours() {
		return DateTimes.dateTimes().ofPrecision(HOURS);
	}

	@Provide
	Arbitrary<LocalDateTime> precisionMinutes() {
		return DateTimes.dateTimes().ofPrecision(MINUTES);
	}

	@Provide
	Arbitrary<LocalDateTime> precisionSeconds() {
		return DateTimes.dateTimes().ofPrecision(SECONDS);
	}

	@Provide
	Arbitrary<LocalDateTime> precisionMilliseconds() {
		return DateTimes.dateTimes().ofPrecision(MILLIS);
	}

	@Provide
	Arbitrary<LocalDateTime> precisionMicroseconds() {
		return DateTimes.dateTimes().ofPrecision(MICROS);
	}

	@Provide
	Arbitrary<LocalDateTime> precisionNanoseconds() {
		return DateTimes.dateTimes().ofPrecision(NANOS);
	}

	@Provide
	Arbitrary<Integer> seconds() {
		return Arbitraries.integers().between(0, 59);
	}

	@Provide
	Arbitrary<Integer> hours() {
		return Arbitraries.integers().between(0, 23);
	}

	@Provide
	Arbitrary<Integer> minutes() {
		return Arbitraries.integers().between(0, 59);
	}

	@Group
	class TimeBetweenMethods {

		@Property
		void timeBetween(@ForAll LocalTime min, @ForAll LocalTime max, @ForAll Random random) {

			Assume.that(!min.isAfter(max));

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().timeBetween(min, max);

			assertAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.toLocalTime()).isAfterOrEqualTo(min);
				assertThat(dateTime.toLocalTime()).isBeforeOrEqualTo(max);
				return true;
			});
		}

		@Property
		void timeBetweenMaxBeforeMin(@ForAll LocalTime min, @ForAll LocalTime max, @ForAll Random random) {

			Assume.that(min.isAfter(max));

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().timeBetween(min, max);

			assertAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.toLocalTime()).isAfterOrEqualTo(max);
				assertThat(dateTime.toLocalTime()).isBeforeOrEqualTo(min);
				return true;
			});
		}

		@Property
		void betweenSame(@ForAll LocalTime same, @ForAll Random random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().timeBetween(same, same);

			assertAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.toLocalTime()).isEqualTo(same);
				return true;
			});

		}

	}

	@Group
	class HourMethods {

		@Property
		void hourBetween(@ForAll("hours") int startHour, @ForAll("hours") int endHour, @ForAll Random random) {

			Assume.that(startHour <= endHour);

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().hourBetween(startHour, endHour);

			assertAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.getHour()).isGreaterThanOrEqualTo(startHour);
				assertThat(dateTime.getHour()).isLessThanOrEqualTo(endHour);
				return true;
			});

		}

		@Property
		void hourBetweenSame(@ForAll("hours") int hour, @ForAll Random random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().hourBetween(hour, hour);

			assertAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.getHour()).isEqualTo(hour);
				return true;
			});

		}

	}

	@Group
	class MinuteMethods {

		@Property
		void minuteBetween(@ForAll("minutes") int startMinute, @ForAll("minutes") int endMinute, @ForAll Random random) {

			Assume.that(startMinute <= endMinute);

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().minuteBetween(startMinute, endMinute);

			assertAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.getMinute()).isGreaterThanOrEqualTo(startMinute);
				assertThat(dateTime.getMinute()).isLessThanOrEqualTo(endMinute);
				return true;
			});

		}

		@Property
		void minuteBetweenSame(@ForAll("minutes") int minute, @ForAll Random random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().minuteBetween(minute, minute);

			assertAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.getMinute()).isEqualTo(minute);
				return true;
			});

		}

	}

	@Group
	class SecondMethods {

		@Property
		void secondBetween(@ForAll("seconds") int startSecond, @ForAll("seconds") int endSecond, @ForAll Random random) {

			Assume.that(startSecond <= endSecond);

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().secondBetween(startSecond, endSecond);

			assertAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.getSecond()).isGreaterThanOrEqualTo(startSecond);
				assertThat(dateTime.getSecond()).isLessThanOrEqualTo(endSecond);
				return true;
			});

		}

		@Property
		void secondBetweenSame(@ForAll("seconds") int second, @ForAll Random random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().secondBetween(second, second);

			assertAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.getSecond()).isEqualTo(second);
				return true;
			});

		}

	}

	@Group
	class PrecisionMethods {

		@Group
		class Hours {

			@Property
			void precision(@ForAll("precisionHours") LocalDateTime dateTime) {
				assertThat(dateTime.getMinute()).isEqualTo(0);
				assertThat(dateTime.getSecond()).isEqualTo(0);
				assertThat(dateTime.getNano()).isEqualTo(0);
			}

			@Property
			void precisionMinTimePrecisionMinutes(@ForAll("precisionMinutes") LocalDateTime min, @ForAll Random random) {

				Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE) || min.getHour() != 23);

				Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(HOURS);

				assertAllGenerated(times.generator(1000), random, dateTime -> {
					assertThat(dateTime.getMinute()).isEqualTo(0);
					assertThat(dateTime.getSecond()).isEqualTo(0);
					assertThat(dateTime.getNano()).isEqualTo(0);
					assertThat(dateTime).isAfterOrEqualTo(min);
					return true;
				});

			}

			@Property
			void precisionMinTime(@ForAll("dateTimes") LocalDateTime min, @ForAll Random random) {

				Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE) || min.getHour() != 23);

				Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(HOURS);

				assertAllGenerated(times.generator(1000), random, dateTime -> {
					assertThat(dateTime.getMinute()).isEqualTo(0);
					assertThat(dateTime.getSecond()).isEqualTo(0);
					assertThat(dateTime.getNano()).isEqualTo(0);
					assertThat(dateTime).isAfterOrEqualTo(min);
					return true;
				});

			}

		}

		@Group
		class Minutes {

			@Property
			void precision(@ForAll("precisionMinutes") LocalDateTime dateTime) {
				assertThat(dateTime.getSecond()).isEqualTo(0);
				assertThat(dateTime.getNano()).isEqualTo(0);
			}

			@Property
			void precisionMinTimePrecisionSeconds(@ForAll("precisionSeconds") LocalDateTime min, @ForAll Random random) {

				Assume.that(!min.toLocalDate()
								.isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE) || min.getHour() != 23 || min.getMinute() != 59);

				Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(MINUTES);

				assertAllGenerated(times.generator(1000), random, dateTime -> {
					assertThat(dateTime.getSecond()).isEqualTo(0);
					assertThat(dateTime.getNano()).isEqualTo(0);
					assertThat(dateTime).isAfterOrEqualTo(min);
					return true;
				});

			}

			@Property
			void precisionMinTime(@ForAll("dateTimes") LocalDateTime min, @ForAll Random random) {

				Assume.that(!min.toLocalDate()
								.isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE) || min.getHour() != 23 || min.getMinute() != 59);

				Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(MINUTES);

				assertAllGenerated(times.generator(1000), random, dateTime -> {
					assertThat(dateTime.getSecond()).isEqualTo(0);
					assertThat(dateTime.getNano()).isEqualTo(0);
					assertThat(dateTime).isAfterOrEqualTo(min);
					return true;
				});

			}

		}

		@Group
		class Seconds {

			@Property
			void precision(@ForAll("precisionSeconds") LocalDateTime dateTime) {
				assertThat(dateTime.getNano()).isEqualTo(0);
			}

			@Property
			void precisionMinTimePrecisionMillis(@ForAll("precisionMilliseconds") LocalDateTime min, @ForAll Random random) {

				Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE)
								|| min.getHour() != 23
								|| min.getMinute() != 59
								|| min.getSecond() != 59);

				Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(SECONDS);

				assertAllGenerated(times.generator(1000), random, dateTime -> {
					assertThat(dateTime.getNano()).isEqualTo(0);
					assertThat(dateTime).isAfterOrEqualTo(min);
					return true;
				});

			}

			@Property
			void precisionMinTime(@ForAll("dateTimes") LocalDateTime min, @ForAll Random random) {

				Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE)
								|| min.getHour() != 23
								|| min.getMinute() != 59
								|| min.getSecond() != 59);

				Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(SECONDS);

				assertAllGenerated(times.generator(1000), random, dateTime -> {
					assertThat(dateTime.getNano()).isEqualTo(0);
					assertThat(dateTime).isAfterOrEqualTo(min);
					return true;
				});

			}

		}

		@Group
		class Milliseconds {

			@Property
			void precision(@ForAll("precisionMilliseconds") LocalDateTime dateTime) {
				assertThat(dateTime.getNano() % 1_000_000).isEqualTo(0);
			}

			@Property
			void precisionMinTimePrecisionMicros(@ForAll("precisionMicroseconds") LocalDateTime min, @ForAll Random random) {

				Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE)
								|| min.getHour() != 23
								|| min.getMinute() != 59
								|| min.getSecond() != 59
								|| min.getNano() < 999_000_001);

				Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(MILLIS);

				assertAllGenerated(times.generator(1000), random, dateTime -> {
					assertThat(dateTime.getNano() % 1_000_000).isEqualTo(0);
					assertThat(dateTime).isAfterOrEqualTo(min);
					return true;
				});

			}

			@Property
			void precisionMinTime(@ForAll("dateTimes") LocalDateTime min, @ForAll Random random) {

				Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE)
								|| min.getHour() != 23
								|| min.getMinute() != 59
								|| min.getSecond() != 59
								|| min.getNano() < 999_000_001);

				Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(MILLIS);

				assertAllGenerated(times.generator(1000), random, dateTime -> {
					assertThat(dateTime.getNano() % 1_000_000).isEqualTo(0);
					assertThat(dateTime).isAfterOrEqualTo(min);
					return true;
				});

			}

		}

		@Group
		class Microseconds {

			@Property
			void precision(@ForAll("precisionMicroseconds") LocalDateTime dateTime) {
				assertThat(dateTime.getNano() % 1_000).isEqualTo(0);
			}

			@Property
			void precisionMinTimePrecisionNanos(@ForAll("precisionNanoseconds") LocalDateTime min, @ForAll Random random) {

				Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE)
								|| min.getHour() != 23
								|| min.getMinute() != 59
								|| min.getSecond() != 59
								|| min.getNano() < 999_999_001);

				Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(MICROS);

				assertAllGenerated(times.generator(1000), random, dateTime -> {
					assertThat(dateTime.getNano() % 1_000).isEqualTo(0);
					assertThat(dateTime).isAfterOrEqualTo(min);
					return true;
				});

			}

			@Property
			void precisionMinTime(@ForAll("dateTimes") LocalDateTime min, @ForAll Random random) {

				Assume.that(!min.toLocalDate().isEqual(DefaultLocalDateArbitrary.DEFAULT_MAX_DATE)
								|| min.getHour() != 23
								|| min.getMinute() != 59
								|| min.getSecond() != 59
								|| min.getNano() < 999_999_001);

				Arbitrary<LocalDateTime> times = DateTimes.dateTimes().atTheEarliest(min).ofPrecision(MICROS);

				assertAllGenerated(times.generator(1000), random, dateTime -> {
					assertThat(dateTime.getNano() % 1_000).isEqualTo(0);
					assertThat(dateTime).isAfterOrEqualTo(min);
					return true;
				});

			}

		}

		@Group
		class Nanos {

			@Property
			void precisionNanoseconds(@ForAll("precisionNanoseconds") LocalDateTime dateTime) {
				assertThat(dateTime).isNotNull();
			}

		}

	}

}
