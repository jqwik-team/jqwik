package net.jqwik.time.api.dates.yearMonth;

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
		YearMonthArbitrary yearMonths = Dates.yearMonths();
		YearMonth value = falsifyThenShrink(yearMonths, random);
		assertThat(value).isEqualTo(YearMonth.of(1900, Month.JANUARY));
	}

	@Property
	void shrinksToSmallestFailingPositiveValue(@ForAll JqwikRandom random) {
		YearMonthArbitrary yearMonths = Dates.yearMonths();
		TestingFalsifier<YearMonth> falsifier = ym -> ym.isBefore(YearMonth.of(2013, Month.MAY));
		YearMonth value = falsifyThenShrink(yearMonths, random, falsifier);
		assertThat(value).isEqualTo(YearMonth.of(2013, Month.MAY));
	}

}
