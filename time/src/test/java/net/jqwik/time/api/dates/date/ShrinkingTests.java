package net.jqwik.time.api.dates.date;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.time.api.testingSupport.ForCalendar.*;
import static net.jqwik.time.api.testingSupport.ForDate.*;

@PropertyDefaults(tries = 100)
public class ShrinkingTests {

	@Property
	void defaultShrinking(@ForAll JqwikRandom random) {
		DateArbitrary dates = Dates.datesAsDate();
		Calendar value = dateToCalendar(falsifyThenShrink(dates, random));
		assertThat(value.get(Calendar.YEAR)).isEqualTo(1900);
		assertThat(value.get(Calendar.MONTH)).isEqualTo(Calendar.JANUARY);
		assertThat(value.get(Calendar.DAY_OF_MONTH)).isEqualTo(1);
	}

	@Property
	void shrinksToSmallestFailingValue(@ForAll JqwikRandom random) {
		DateArbitrary dates = Dates.datesAsDate();
		Calendar calendar = getCalendar(2013, Calendar.MAY, 24);
		TestingFalsifier<Date> falsifier = date -> !dateToCalendar(date).after(calendar);
		Calendar value = dateToCalendar(falsifyThenShrink(dates, random, falsifier));
		assertThat(value.get(Calendar.YEAR)).isEqualTo(2013);
		assertThat(value.get(Calendar.MONTH)).isEqualTo(Calendar.MAY);
		assertThat(value.get(Calendar.DAY_OF_MONTH)).isEqualTo(25);
	}

}
