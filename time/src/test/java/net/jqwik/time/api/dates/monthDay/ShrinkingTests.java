package net.jqwik.time.api.dates.monthDay;

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
		MonthDayArbitrary monthDays = Dates.monthDays();
		MonthDay value = falsifyThenShrink(monthDays, random);
		assertThat(value).isEqualTo(MonthDay.of(Month.JANUARY, 1));
	}

	@Property
	void shrinksToSmallestFailingValue(@ForAll JqwikRandom random) {
		MonthDayArbitrary monthDays = Dates.monthDays();
		TestingFalsifier<MonthDay> falsifier = md -> md.isBefore(MonthDay.of(Month.MAY, 25));
		MonthDay value = falsifyThenShrink(monthDays, random, falsifier);
		assertThat(value).isEqualTo(MonthDay.of(Month.MAY, 25));
	}

}
