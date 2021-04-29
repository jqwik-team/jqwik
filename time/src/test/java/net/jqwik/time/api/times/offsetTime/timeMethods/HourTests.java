package net.jqwik.time.api.times.offsetTime.timeMethods;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

public class HourTests {

	@Property
	void hourBetween(@ForAll("hours") int startHour, @ForAll("hours") int endHour, @ForAll Random random) {

		Assume.that(startHour <= endHour);

		Arbitrary<OffsetTime> times = Times.offsetTimes().hourBetween(startHour, endHour);

		assertAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.getHour()).isGreaterThanOrEqualTo(startHour);
			assertThat(time.getHour()).isLessThanOrEqualTo(endHour);
			return true;
		});

	}

	@Property
	void hourBetweenSame(@ForAll("hours") int hour, @ForAll Random random) {

		Arbitrary<OffsetTime> times = Times.offsetTimes().hourBetween(hour, hour);

		assertAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.getHour()).isEqualTo(hour);
			return true;
		});

	}

	@Provide
	Arbitrary<Integer> hours() {
		return Arbitraries.integers().between(0, 23);
	}

}
