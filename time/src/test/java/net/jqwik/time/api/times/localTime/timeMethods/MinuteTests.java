package net.jqwik.time.api.times.localTime.timeMethods;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 100)
public class MinuteTests {

	@Provide
	Arbitrary<Integer> minutes() {
		return Arbitraries.integers().between(0, 59);
	}

	@Property
	void minuteBetween(@ForAll("minutes") int startMinute, @ForAll("minutes") int endMinute, @ForAll JqwikRandom random) {

		Assume.that(startMinute <= endMinute);

		Arbitrary<LocalTime> times = Times.times().minuteBetween(startMinute, endMinute);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.getMinute()).isGreaterThanOrEqualTo(startMinute);
			assertThat(time.getMinute()).isLessThanOrEqualTo(endMinute);
			return true;
		});

	}

	@Property
	void minuteBetweenSame(@ForAll("minutes") int minute, @ForAll JqwikRandom random) {

		Arbitrary<LocalTime> times = Times.times().minuteBetween(minute, minute);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.getMinute()).isEqualTo(minute);
			return true;
		});

	}

}
