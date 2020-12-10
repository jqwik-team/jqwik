package net.jqwik.api.time;

import java.time.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

class DatesTests {

	@Property
	void validLocalDateIsGenerated(@ForAll("dates") LocalDate localDate) {
		Assertions.assertThat(localDate).isNotNull();
	}

	@Provide
	Arbitrary<LocalDate> dates() {
		return Dates.dates();
	}
}
