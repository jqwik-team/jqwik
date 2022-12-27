package net.jqwik.time.api.times.offsetTime.timeMethods;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 100)
public class TimeTests {

	@Property
	void atTheEarliest(@ForAll LocalTime startTime, @ForAll JqwikRandom random) {

		Arbitrary<OffsetTime> times = Times.offsetTimes().atTheEarliest(startTime);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.toLocalTime()).isAfterOrEqualTo(startTime);
			return true;
		});

	}

	@Property
	void atTheLatest(@ForAll LocalTime endTime, @ForAll JqwikRandom random) {

		Arbitrary<OffsetTime> times = Times.offsetTimes().atTheLatest(endTime);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.toLocalTime()).isBeforeOrEqualTo(endTime);
			return true;
		});

	}

	@Property
	void between(@ForAll LocalTime startTime, @ForAll LocalTime endTime, @ForAll JqwikRandom random) {

		Assume.that(!startTime.isAfter(endTime));

		Arbitrary<OffsetTime> times = Times.offsetTimes().between(startTime, endTime);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.toLocalTime()).isAfterOrEqualTo(startTime);
			assertThat(time.toLocalTime()).isBeforeOrEqualTo(endTime);
			return true;
		});
	}

	@Property
	void betweenEndTimeBeforeStartTime(
		@ForAll LocalTime startTime,
		@ForAll LocalTime endTime,
		@ForAll JqwikRandom random
	) {

		Assume.that(startTime.isAfter(endTime));

		Arbitrary<OffsetTime> times = Times.offsetTimes().between(startTime, endTime);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.toLocalTime()).isAfterOrEqualTo(endTime);
			assertThat(time.toLocalTime()).isBeforeOrEqualTo(startTime);
			return true;
		});
	}

	@Property
	void betweenSame(@ForAll LocalTime sameTime, @ForAll JqwikRandom random) {

		Arbitrary<OffsetTime> times = Times.offsetTimes().between(sameTime, sameTime);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.toLocalTime()).isEqualTo(sameTime);
			return true;
		});

	}

}
