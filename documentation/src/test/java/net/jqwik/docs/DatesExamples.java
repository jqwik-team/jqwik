package net.jqwik.docs;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

class DatesExamples {

	@Property
	void generateLocalDatesWithAnnotation(@ForAll @DateRange(min = "2019-01-01", max = "2020-12-31") LocalDate localDate) {
		assertThat(localDate).isBetween(
				LocalDate.of(2019, 1, 1),
				LocalDate.of(2020, 12, 31)
		);
	}

	@Property
	void generateLocalDates(@ForAll("dates") LocalDate localDate) {
		assertThat(localDate).isAfter(LocalDate.of(2000, 12, 31));
	}

	@Provide
	Arbitrary<LocalDate> dates() {
		return Dates.dates().atTheEarliest(LocalDate.of(2001, 1, 1));
	}

}
