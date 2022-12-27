package net.jqwik.time.api.dateTimes.localDateTime.dateTimeMethods;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 100)
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
		void timeBetween(@ForAll LocalTime min, @ForAll LocalTime max, @ForAll JqwikRandom random) {

			Assume.that(!min.isAfter(max));

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().timeBetween(min, max);

			checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.toLocalTime()).isAfterOrEqualTo(min);
				assertThat(dateTime.toLocalTime()).isBeforeOrEqualTo(max);
				return true;
			});
		}

		@Property
		void timeBetweenMaxBeforeMin(@ForAll LocalTime min, @ForAll LocalTime max, @ForAll JqwikRandom random) {

			Assume.that(min.isAfter(max));

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().timeBetween(min, max);

			checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.toLocalTime()).isAfterOrEqualTo(max);
				assertThat(dateTime.toLocalTime()).isBeforeOrEqualTo(min);
				return true;
			});
		}

		@Property
		void betweenSame(@ForAll LocalTime same, @ForAll JqwikRandom random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().timeBetween(same, same);

			checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.toLocalTime()).isEqualTo(same);
				return true;
			});

		}

	}

	@Group
	class HourMethods {

		@Property
		void hourBetween(@ForAll("hours") int startHour, @ForAll("hours") int endHour, @ForAll JqwikRandom random) {

			Assume.that(startHour <= endHour);

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().hourBetween(startHour, endHour);

			checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.getHour()).isGreaterThanOrEqualTo(startHour);
				assertThat(dateTime.getHour()).isLessThanOrEqualTo(endHour);
				return true;
			});

		}

		@Property
		void hourBetweenSame(@ForAll("hours") int hour, @ForAll JqwikRandom random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().hourBetween(hour, hour);

			checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.getHour()).isEqualTo(hour);
				return true;
			});

		}

	}

	@Group
	class MinuteMethods {

		@Property
		void minuteBetween(@ForAll("minutes") int startMinute, @ForAll("minutes") int endMinute, @ForAll JqwikRandom random) {

			Assume.that(startMinute <= endMinute);

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().minuteBetween(startMinute, endMinute);

			checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.getMinute()).isGreaterThanOrEqualTo(startMinute);
				assertThat(dateTime.getMinute()).isLessThanOrEqualTo(endMinute);
				return true;
			});

		}

		@Property
		void minuteBetweenSame(@ForAll("minutes") int minute, @ForAll JqwikRandom random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().minuteBetween(minute, minute);

			checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.getMinute()).isEqualTo(minute);
				return true;
			});

		}

	}

	@Group
	class SecondMethods {

		@Property
		void secondBetween(@ForAll("seconds") int startSecond, @ForAll("seconds") int endSecond, @ForAll JqwikRandom random) {

			Assume.that(startSecond <= endSecond);

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().secondBetween(startSecond, endSecond);

			checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.getSecond()).isGreaterThanOrEqualTo(startSecond);
				assertThat(dateTime.getSecond()).isLessThanOrEqualTo(endSecond);
				return true;
			});

		}

		@Property
		void secondBetweenSame(@ForAll("seconds") int second, @ForAll JqwikRandom random) {

			Arbitrary<LocalDateTime> dateTimes = DateTimes.dateTimes().secondBetween(second, second);

			checkAllGenerated(dateTimes.generator(1000), random, dateTime -> {
				assertThat(dateTime.getSecond()).isEqualTo(second);
				return true;
			});

		}

	}

	@Group
	class PrecisionMethods {

		@Property
		void hours(@ForAll("precisionHours") LocalDateTime dateTime) {
			assertThat(dateTime.getMinute()).isEqualTo(0);
			assertThat(dateTime.getSecond()).isEqualTo(0);
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void minutes(@ForAll("precisionMinutes") LocalDateTime dateTime) {
			assertThat(dateTime.getSecond()).isEqualTo(0);
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void seconds(@ForAll("precisionSeconds") LocalDateTime dateTime) {
			assertThat(dateTime.getNano()).isEqualTo(0);
		}

		@Property
		void milliseconds(@ForAll("precisionMilliseconds") LocalDateTime dateTime) {
			assertThat(dateTime.getNano() % 1_000_000).isEqualTo(0);
		}

		@Property
		void microseconds(@ForAll("precisionMicroseconds") LocalDateTime dateTime) {
			assertThat(dateTime.getNano() % 1_000).isEqualTo(0);
		}

		@Property
		void nanoseconds(@ForAll("precisionNanoseconds") LocalDateTime dateTime) {
			assertThat(dateTime).isNotNull();
		}

	}

}
