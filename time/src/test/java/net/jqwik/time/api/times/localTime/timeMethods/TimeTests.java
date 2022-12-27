package net.jqwik.time.api.times.localTime.timeMethods;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 100)
public class TimeTests {

	@Provide
	Arbitrary<LocalTime> times() {
		return Times.times();
	}

	@Property
	void atTheEarliest(@ForAll("times") LocalTime startTime, @ForAll JqwikRandom random) {

		Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time).isAfterOrEqualTo(startTime);
			return true;
		});

	}

	@Property
	void atTheEarliestAtTheLatestMinAfterMax(
		@ForAll("times") LocalTime startTime,
		@ForAll("times") LocalTime endTime,
		@ForAll JqwikRandom random
	) {

		Assume.that(startTime.isAfter(endTime));

		Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime).atTheLatest(endTime);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time).isAfterOrEqualTo(endTime);
			assertThat(time).isBeforeOrEqualTo(startTime);
			return true;
		});

	}

	@Property
	void atTheLatest(@ForAll("times") LocalTime endTime, @ForAll JqwikRandom random) {

		Arbitrary<LocalTime> times = Times.times().atTheLatest(endTime);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time).isBeforeOrEqualTo(endTime);
			return true;
		});

	}

	@Property
	void atTheLatestAtTheEarliestMinAfterMax(
		@ForAll("times") LocalTime startTime,
		@ForAll("times") LocalTime endTime,
		@ForAll JqwikRandom random
	) {

		Assume.that(startTime.isAfter(endTime));

		Arbitrary<LocalTime> times = Times.times().atTheLatest(endTime).atTheEarliest(startTime);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time).isAfterOrEqualTo(endTime);
			assertThat(time).isBeforeOrEqualTo(startTime);
			return true;
		});

	}

	@Property
	void between(@ForAll("times") LocalTime startTime, @ForAll("times") LocalTime endTime, @ForAll JqwikRandom random) {

		Assume.that(!startTime.isAfter(endTime));

		Arbitrary<LocalTime> times = Times.times().between(startTime, endTime);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time).isAfterOrEqualTo(startTime);
			assertThat(time).isBeforeOrEqualTo(endTime);
			return true;
		});
	}

	@Property
	void betweenEndTimeBeforeStartTime(
		@ForAll("times") LocalTime startTime,
		@ForAll("times") LocalTime endTime,
		@ForAll JqwikRandom random
	) {

		Assume.that(startTime.isAfter(endTime));

		Arbitrary<LocalTime> times = Times.times().between(startTime, endTime);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time).isAfterOrEqualTo(endTime);
			assertThat(time).isBeforeOrEqualTo(startTime);
			return true;
		});
	}

	@Property
	void betweenSame(@ForAll("times") LocalTime sameTime, @ForAll JqwikRandom random) {

		Arbitrary<LocalTime> times = Times.times().between(sameTime, sameTime);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time).isEqualTo(sameTime);
			return true;
		});

	}

}
