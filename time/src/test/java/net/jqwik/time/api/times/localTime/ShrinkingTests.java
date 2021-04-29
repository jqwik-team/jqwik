package net.jqwik.time.api.times.localTime;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

public class ShrinkingTests {

	@Property
	void defaultShrinking(@ForAll Random random) {
		LocalTimeArbitrary times = Times.times();
		LocalTime value = falsifyThenShrink(times, random);
		assertThat(value).isEqualTo(LocalTime.of(0, 0, 0, 0));
	}

	@Property(tries = 100)
	void shrinksToSmallestFailingValue(@ForAll Random random) {
		LocalTimeArbitrary times = Times.times().ofPrecision(SECONDS);
		TestingFalsifier<LocalTime> falsifier = time -> time.isBefore(LocalTime.of(9, 13, 42, 143_921_111));
		LocalTime value = falsifyThenShrink(times, random, falsifier);
		assertThat(value).isEqualTo(LocalTime.of(9, 13, 43, 0));
	}

}
