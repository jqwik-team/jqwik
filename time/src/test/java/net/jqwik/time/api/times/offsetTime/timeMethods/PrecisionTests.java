package net.jqwik.time.api.times.offsetTime.timeMethods;

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
	Arbitrary<OffsetTime> times() {
		return Times.offsetTimes();
	}

	@Provide
	Arbitrary<OffsetTime> precisionHours() {
		return Times.offsetTimes().ofPrecision(HOURS);
	}

	@Provide
	Arbitrary<OffsetTime> precisionMinutes() {
		return Times.offsetTimes().ofPrecision(MINUTES);
	}

	@Provide
	Arbitrary<OffsetTime> precisionSeconds() {
		return Times.offsetTimes().ofPrecision(SECONDS);
	}

	@Provide
	Arbitrary<OffsetTime> precisionMilliseconds() {
		return Times.offsetTimes().ofPrecision(MILLIS);
	}

	@Provide
	Arbitrary<OffsetTime> precisionMicroseconds() {
		return Times.offsetTimes().ofPrecision(MICROS);
	}

	@Provide
	Arbitrary<OffsetTime> precisionNanoseconds() {
		return Times.offsetTimes().ofPrecision(NANOS);
	}

	@Group
	class Hours {

		@Property
		void precision(@ForAll("precisionHours") OffsetTime time) {
			assertThat(time.getMinute()).isEqualTo(0);
			assertThat(time.getSecond()).isEqualTo(0);
			assertThat(time.getNano()).isEqualTo(0);
		}

		@Property
		void precisionMinTime(@ForAll("precisionMinutes") OffsetTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23);

			Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime.toLocalTime()).ofPrecision(HOURS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getMinute()).isEqualTo(0);
				assertThat(time.getSecond()).isEqualTo(0);
				assertThat(time.getNano()).isEqualTo(0);
				assertThat(time.toLocalTime()).isAfterOrEqualTo(startTime.toLocalTime());
				return true;
			});

		}

		@Property
		void precisionMinTime2(@ForAll("times") OffsetTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23);

			Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime.toLocalTime()).ofPrecision(HOURS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getMinute()).isEqualTo(0);
				assertThat(time.getSecond()).isEqualTo(0);
				assertThat(time.getNano()).isEqualTo(0);
				assertThat(time.toLocalTime()).isAfterOrEqualTo(startTime.toLocalTime());
				return true;
			});

		}

	}

	@Group
	class Minutes {

		@Property
		void precision(@ForAll("precisionMinutes") OffsetTime time) {
			assertThat(time.getSecond()).isEqualTo(0);
			assertThat(time.getNano()).isEqualTo(0);
		}

		@Property
		void precisionMinTime(@ForAll("precisionSeconds") OffsetTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59);

			Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime.toLocalTime()).ofPrecision(MINUTES);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getSecond()).isEqualTo(0);
				assertThat(time.getNano()).isEqualTo(0);
				assertThat(time.toLocalTime()).isAfterOrEqualTo(startTime.toLocalTime());
				return true;
			});

		}

		@Property
		void precisionMinTime2(@ForAll("times") OffsetTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59);

			Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime.toLocalTime()).ofPrecision(MINUTES);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getSecond()).isEqualTo(0);
				assertThat(time.getNano()).isEqualTo(0);
				assertThat(time.toLocalTime()).isAfterOrEqualTo(startTime.toLocalTime());
				return true;
			});

		}

	}

	@Group
	class Seconds {

		@Property
		void precision(@ForAll("precisionSeconds") OffsetTime time) {
			assertThat(time.getNano()).isEqualTo(0);
		}

		@Property
		void precisionMinTime(@ForAll("precisionMilliseconds") OffsetTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59);

			Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime.toLocalTime()).ofPrecision(SECONDS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getNano()).isEqualTo(0);
				assertThat(time.toLocalTime()).isAfterOrEqualTo(startTime.toLocalTime());
				return true;
			});

		}

		@Property
		void precisionMinTime2(@ForAll("times") OffsetTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23 || startTime.getMinute() != 59 || startTime.getSecond() != 59);

			Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime.toLocalTime()).ofPrecision(SECONDS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getNano()).isEqualTo(0);
				assertThat(time.toLocalTime()).isAfterOrEqualTo(startTime.toLocalTime());
				return true;
			});

		}

	}

	@Group
	class Milliseconds {

		@Property
		void precision(@ForAll("precisionMilliseconds") OffsetTime time) {
			assertThat(time.getNano() % 1_000_000).isEqualTo(0);
		}

		@Property
		void precisionMinTime(@ForAll("precisionMicroseconds") OffsetTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23
							|| startTime.getMinute() != 59
							|| startTime.getSecond() != 59
							|| startTime.getNano() < 999_000_001);

			Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime.toLocalTime()).ofPrecision(MILLIS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getNano() % 1_000_000).isEqualTo(0);
				assertThat(time.toLocalTime()).isAfterOrEqualTo(startTime.toLocalTime());
				return true;
			});

		}

		@Property
		void precisionMinTime2(@ForAll("times") OffsetTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23
							|| startTime.getMinute() != 59
							|| startTime.getSecond() != 59
							|| startTime.getNano() < 999_000_001);

			Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime.toLocalTime()).ofPrecision(MILLIS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getNano() % 1_000_000).isEqualTo(0);
				assertThat(time.toLocalTime()).isAfterOrEqualTo(startTime.toLocalTime());
				return true;
			});

		}

	}

	@Group
	class Microseconds {

		@Property
		void precision(@ForAll("precisionMicroseconds") OffsetTime time) {
			assertThat(time.getNano() % 1_000).isEqualTo(0);
		}

		@Property
		void precisionMinTime(@ForAll("times") OffsetTime startTime, @ForAll Random random) {

			Assume.that(startTime.getHour() != 23
							|| startTime.getMinute() != 59
							|| startTime.getSecond() != 59
							|| startTime.getNano() < 999_999_001);

			Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime.toLocalTime()).ofPrecision(MICROS);

			assertAllGenerated(times.generator(1000), random, time -> {
				assertThat(time.getNano() % 1_000).isEqualTo(0);
				assertThat(time.toLocalTime()).isAfterOrEqualTo(startTime.toLocalTime());
				return true;
			});

		}

	}

	@Group
	class Nanos {

		@Property
		void precision(@ForAll("precisionNanoseconds") OffsetTime time) {
			assertThat(time).isNotNull();
		}

	}

}
