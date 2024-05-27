package net.jqwik.time.api.dates.year;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

@PropertyDefaults(tries = 100)
public class ShrinkingTests {

	@Property
	void defaultShrinking(@ForAll Random random) {
		YearArbitrary years = Dates.years();
		Year value = falsifyThenShrink(years, random);
		assertThat((Object) value).isEqualTo(Year.of(1900));
	}

	@Property
	void shrinksToSmallestFailingPositiveValue(@ForAll Random random) {
		YearArbitrary years = Dates.years();
		TestingFalsifier<Year> falsifier = year -> year.getValue() < 1942;
		Year value = falsifyThenShrink(years, random, falsifier);
		assertThat((Object) value).isEqualTo(Year.of(1942));
	}

}
