package net.jqwik.time.api.dates.monthDay;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<MonthDay> monthDays() {
		return Dates.monthDays();
	}

	@Property
	void validMonthDayIsGenerated(@ForAll("monthDays") MonthDay monthDay) {
		assertThat(monthDay).isNotNull();
	}

}
