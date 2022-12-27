package net.jqwik.time.api.times.offsetTime.timeMethods;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 100)
public class SecondTests {

	@Property
	void secondBetween(@ForAll("seconds") int startSecond, @ForAll("seconds") int endSecond, @ForAll JqwikRandom random) {

		Assume.that(startSecond <= endSecond);

		Arbitrary<OffsetTime> times = Times.offsetTimes().secondBetween(startSecond, endSecond);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.getSecond()).isGreaterThanOrEqualTo(startSecond);
			assertThat(time.getSecond()).isLessThanOrEqualTo(endSecond);
			return true;
		});

	}

	@Property
	void secondBetweenSame(@ForAll("seconds") int second, @ForAll JqwikRandom random) {

		Arbitrary<OffsetTime> times = Times.offsetTimes().secondBetween(second, second);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.getSecond()).isEqualTo(second);
			return true;
		});

	}

	@Provide
	Arbitrary<Integer> seconds() {
		return Arbitraries.integers().between(0, 59);
	}

}
