package net.jqwik.time.api.times.localTime.timeMethods;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
public class PrecisionTests {

	@Provide
	Arbitrary<LocalTime> times() {
		return Times.times();
	}

	@Provide
	Arbitrary<LocalTime> precisionHours() {
		return Times.times().ofPrecision(HOURS);
	}

	@Provide
	Arbitrary<LocalTime> precisionMinutes() {
		return Times.times().ofPrecision(MINUTES);
	}

	@Provide
	Arbitrary<LocalTime> precisionSeconds() {
		return Times.times().ofPrecision(SECONDS);
	}

	@Provide
	Arbitrary<LocalTime> precisionMilliseconds() {
		return Times.times().ofPrecision(MILLIS);
	}

	@Provide
	Arbitrary<LocalTime> precisionMicroseconds() {
		return Times.times().ofPrecision(MICROS);
	}

	@Provide
	Arbitrary<LocalTime> precisionNanoseconds() {
		return Times.times().ofPrecision(NANOS);
	}

	@Group
	class Hours {

		@Property
		void precision(@ForAll("precisionHours") LocalTime time) {
			assertThat(time.getMinute()).isEqualTo(0);
			assertThat(time.getSecond()).isEqualTo(0);
			assertThat(time.getNano()).isEqualTo(0);
		}

		@Property
		void precisionMinTime(@ForAll("precisionMinutes") LocalTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23);

			Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(HOURS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getMinute()).isEqualTo(0);
				assertThat(time.getSecond()).isEqualTo(0);
				assertThat(time.getNano()).isEqualTo(0);
				assertThat(time).isAfterOrEqualTo(startTime);
				return true;
			});

		}

		@Property
		void precisionMinTime2(@ForAll("times") LocalTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23);

			Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(HOURS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getMinute()).isEqualTo(0);
				assertThat(time.getSecond()).isEqualTo(0);
				assertThat(time.getNano()).isEqualTo(0);
				assertThat(time).isAfterOrEqualTo(startTime);
				return true;
			});

		}

	}

	@Group
	class Minutes {

		@Property
		void precision(@ForAll("precisionMinutes") LocalTime time) {
			assertThat(time.getSecond()).isEqualTo(0);
			assertThat(time.getNano()).isEqualTo(0);
		}

		@Property
		void precisionMinTime(@ForAll("precisionSeconds") LocalTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59);

			Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(MINUTES);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getSecond()).isEqualTo(0);
				assertThat(time.getNano()).isEqualTo(0);
				assertThat(time).isAfterOrEqualTo(startTime);
				return true;
			});

		}

		@Property
		void precisionMinTime2(@ForAll("times") LocalTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59);

			Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(MINUTES);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getSecond()).isEqualTo(0);
				assertThat(time.getNano()).isEqualTo(0);
				assertThat(time).isAfterOrEqualTo(startTime);
				return true;
			});

		}

	}

	@Group
	class Seconds {

		@Property
		void precision(@ForAll("precisionSeconds") LocalTime time) {
			assertThat(time.getNano()).isEqualTo(0);
		}

		@Property
		void precisionMinTime(@ForAll("precisionMilliseconds") LocalTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59);

			Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(SECONDS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getNano()).isEqualTo(0);
				assertThat(time).isAfterOrEqualTo(startTime);
				return true;
			});

		}

		@Property
		void precisionMinTime2(@ForAll("times") LocalTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59);

			Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(SECONDS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getNano()).isEqualTo(0);
				assertThat(time).isAfterOrEqualTo(startTime);
				return true;
			});

		}

	}

	@Group
	class Milliseconds {

		@Property
		void precision(@ForAll("precisionMilliseconds") LocalTime time) {
			assertThat(time.getNano() % 1_000_000).isEqualTo(0);
		}

		@Property
		void precisionMinTime(@ForAll("precisionMicroseconds") LocalTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59 || startTime
																													   .getNano() < 999_000_001);

			Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(MILLIS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getNano() % 1_000_000).isEqualTo(0);
				assertThat(time).isAfterOrEqualTo(startTime);
				return true;
			});

		}

		@Property
		void precisionMinTime2(@ForAll("times") LocalTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59 || startTime
																													   .getNano() < 999_000_001);

			Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(MILLIS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getNano() % 1_000_000).isEqualTo(0);
				assertThat(time).isAfterOrEqualTo(startTime);
				return true;
			});

		}

	}

	@Group
	class Microseconds {

		@Property
		void precision(@ForAll("precisionMicroseconds") LocalTime time) {
			assertThat(time.getNano() % 1_000).isEqualTo(0);
		}

		@Property
		void precisionMinTime(@ForAll("times") LocalTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59 || startTime
																													   .getNano() < 999_999_001);

			Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).ofPrecision(MICROS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getNano() % 1_000).isEqualTo(0);
				assertThat(time).isAfterOrEqualTo(startTime);
				return true;
			});

		}

	}

	@Group
	class Nanos {

		@Property
		void precisionNanoseconds(@ForAll("precisionNanoseconds") LocalTime time) {
			assertThat(time).isNotNull();
		}

	}

}
