package net.jqwik.time.api.dates.monthDay;

import java.time.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

public class DefaultGenerationTests {

	@Property
	void validMonthDayIsGenerated(@ForAll MonthDay monthDay) {
		assertThat(monthDay).isNotNull();
	}

}
