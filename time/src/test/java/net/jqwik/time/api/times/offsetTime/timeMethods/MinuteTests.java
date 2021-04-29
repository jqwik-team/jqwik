package net.jqwik.time.api.times.offsetTime.timeMethods;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

public class MinuteTests {

	@Property
	void minuteBetween(@ForAll("minutes") int startMinute, @ForAll("minutes") int endMinute, @ForAll Random random) {

		Assume.that(startMinute <= endMinute);

		Arbitrary<OffsetTime> times = Times.offsetTimes().minuteBetween(startMinute, endMinute);

		assertAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.getMinute()).isGreaterThanOrEqualTo(startMinute);
			assertThat(time.getMinute()).isLessThanOrEqualTo(endMinute);
			return true;
		});

	}

	@Property
	void minuteBetweenSame(@ForAll("minutes") int minute, @ForAll Random random) {

		Arbitrary<OffsetTime> times = Times.offsetTimes().minuteBetween(minute, minute);

		assertAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.getMinute()).isEqualTo(minute);
			return true;
		});

	}

	@Provide
	Arbitrary<Integer> minutes() {
		return Arbitraries.integers().between(0, 59);
	}

}
