package net.jqwik.time.api.dateTimes.localDateTime;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

public class ShrinkingTests {

	@Property
	void defaultShrinking(@ForAll JqwikRandom random) {
		LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes();
		LocalDateTime value = falsifyThenShrink(dateTimes, random);
		assertThat(value).isEqualTo(LocalDateTime.of(1900, JANUARY, 1, 0, 0, 0));
	}

	@Property(tries = 40)
	void shrinksToSmallestFailingValue(@ForAll JqwikRandom random) {
		LocalDateTimeArbitrary dateTimes = DateTimes.dateTimes();
		TestingFalsifier<LocalDateTime> falsifier = dateTime -> dateTime.isBefore(LocalDateTime.of(2013, MAY, 25, 13, 12, 55));
		LocalDateTime value = falsifyThenShrink(dateTimes, random, falsifier);
		assertThat(value).isEqualTo(LocalDateTime.of(2013, MAY, 25, 13, 12, 55));
	}

}
