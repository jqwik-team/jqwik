package net.jqwik.time.api.dates.year;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class DefaultGenerationTests {

	@Property
	void yearIsNotNull(@ForAll Year year) {
		assertThat((Object) year).isNotNull();
	}

	@Property
	void defaultYearGenerationYearsOnlyBetween1900And2500(@ForAll Year year) {
		assertThat(year.getValue()).isGreaterThanOrEqualTo(1900);
		assertThat(year.getValue()).isLessThanOrEqualTo(2500);
	}

	@Property
	void yearIsNotZero(@ForAll Year year) {
		assertThat((Object) year).isNotEqualTo(Year.of(0));
	}

}
