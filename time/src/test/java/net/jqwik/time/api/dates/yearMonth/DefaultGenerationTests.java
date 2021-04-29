package net.jqwik.time.api.dates.yearMonth;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class DefaultGenerationTests {

	@Property
	void validYearMonthIsGenerated(@ForAll YearMonth yearMonth) {
		assertThat(yearMonth).isNotNull();
	}

}
