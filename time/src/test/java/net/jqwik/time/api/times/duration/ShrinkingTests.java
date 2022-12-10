package net.jqwik.time.api.times.duration;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

public class ShrinkingTests {

	@Property
	void defaultShrinking(@ForAll JqwikRandom random) {
		DurationArbitrary durations = Times.durations();
		Duration value = falsifyThenShrink(durations, random);
		assertThat(value).isEqualTo(Duration.ofSeconds(0, 0));
	}

	@Property(tries = 40)
	void shrinksToSmallestFailingPositiveValue(@ForAll JqwikRandom random) {
		DurationArbitrary durations = Times.durations();
		TestingFalsifier<Duration> falsifier = duration -> duration.compareTo(Duration.ofSeconds(999_392_192, 709_938_291)) < 0;
		Duration value = falsifyThenShrink(durations, random, falsifier);
		assertThat(value).isEqualTo(Duration.ofSeconds(999_392_193));
	}

	@Property(tries = 10)
	void shrinksToSmallestFailingNegativeValue(@ForAll JqwikRandom random) {
		DurationArbitrary durations = Times.durations();
		TestingFalsifier<Duration> falsifier = duration -> duration.compareTo(Duration.ofSeconds(-999_392_192, 709_938_291)) > 0;
		Duration value = falsifyThenShrink(durations, random, falsifier);
		assertThat(value).isEqualTo(Duration.ofSeconds(-999_392_192));
	}

}
