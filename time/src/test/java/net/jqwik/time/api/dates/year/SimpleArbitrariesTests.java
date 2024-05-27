package net.jqwik.time.api.dates.year;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<Year> years() {
		return Dates.years();
	}

	@Provide
	Arbitrary<Year> yearsAround0() {
		return Dates.years().between(-10, 10);
	}

	@Property
	void yearIsNotNull(@ForAll("years") Year year) {
		assertThat((Object) year).isNotNull();
	}

	@Property
	void defaultYearGenerationYearsOnlyBetween1900And2500(@ForAll("years") Year year) {
		assertThat(year.getValue()).isGreaterThanOrEqualTo(1900);
		assertThat(year.getValue()).isLessThanOrEqualTo(2500);
	}

	@Property
	void yearIsNotZero(@ForAll("yearsAround0") Year year) {
		assertThat((Object) year).isNotEqualTo(Year.of(0));
	}

}
