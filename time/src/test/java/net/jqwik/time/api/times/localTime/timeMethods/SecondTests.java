package net.jqwik.time.api.times.localTime.timeMethods;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 100)
public class SecondTests {

	@Provide
	Arbitrary<Integer> seconds() {
		return Arbitraries.integers().between(0, 59);
	}

	@Property
	void secondBetween(@ForAll("seconds") int startSecond, @ForAll("seconds") int endSecond, @ForAll JqwikRandom random) {

		Assume.that(startSecond <= endSecond);

		Arbitrary<LocalTime> times = Times.times().secondBetween(startSecond, endSecond);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.getSecond()).isGreaterThanOrEqualTo(startSecond);
			assertThat(time.getSecond()).isLessThanOrEqualTo(endSecond);
			return true;
		});

	}

	@Property
	void secondBetweenSame(@ForAll("seconds") int second, @ForAll JqwikRandom random) {

		Arbitrary<LocalTime> times = Times.times().secondBetween(second, second);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.getSecond()).isEqualTo(second);
			return true;
		});

	}

}
