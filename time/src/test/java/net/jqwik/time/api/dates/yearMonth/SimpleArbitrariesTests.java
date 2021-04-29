package net.jqwik.time.api.dates.yearMonth;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<YearMonth> yearMonths() {
		return Dates.yearMonths();
	}

	@Property
	void validYearMonthIsGenerated(@ForAll("yearMonths") YearMonth yearMonth) {
		assertThat(yearMonth).isNotNull();
	}

}
