package net.jqwik.time.api.dates.year;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

public class YearMethodsTests {

	@Provide
	Arbitrary<Year> years() {
		return Dates.years();
	}

	@Property
	void between(@ForAll("years") Year startYear, @ForAll("years") Year endYear, @ForAll Random random) {

		Assume.that(startYear.compareTo(endYear) <= 0);

		Arbitrary<Year> years = Dates.years().between(startYear, endYear);

		checkAllGenerated(years.generator(1000, true), random, year -> {
			assertThat(year.getValue()).isGreaterThanOrEqualTo(startYear.getValue());
			assertThat(year.getValue()).isLessThanOrEqualTo(endYear.getValue());
			return true;
		});

	}

	@Property
	void betweenSame(@ForAll("years") Year year, @ForAll Random random) {

		Arbitrary<Year> years = Dates.years().between(year, year);

		checkAllGenerated(years.generator(1000, true), random, y -> {
			assertThat((Object) y).isEqualTo(year);
			return true;
		});

	}

}
