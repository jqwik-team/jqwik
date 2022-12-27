package net.jqwik.time.api.dates.localDate;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

@PropertyDefaults(tries = 100)
public class ShrinkingTests {

	@Property
	void defaultShrinking(@ForAll JqwikRandom random) {
		LocalDateArbitrary dates = Dates.dates();
		LocalDate value = falsifyThenShrink(dates, random);
		assertThat(value).isEqualTo(LocalDate.of(1900, JANUARY, 1));
	}

	@Property
	void shrinksToSmallestFailingValue(@ForAll JqwikRandom random) {
		LocalDateArbitrary dates = Dates.dates();
		TestingFalsifier<LocalDate> falsifier = date -> date.isBefore(LocalDate.of(2013, MAY, 25));
		LocalDate value = falsifyThenShrink(dates, random, falsifier);
		assertThat(value).isEqualTo(LocalDate.of(2013, MAY, 25));
	}

}
