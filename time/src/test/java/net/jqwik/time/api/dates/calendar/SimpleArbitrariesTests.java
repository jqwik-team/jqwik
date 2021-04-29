package net.jqwik.time.api.dates.calendar;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

public class SimpleArbitrariesTests {

	@Provide
	Arbitrary<Calendar> dates() {
		return Dates.datesAsCalendar();
	}

	@Property
	void validCalendarIsGenerated(@ForAll("dates") Calendar calendar) {
		assertThat(calendar).isNotNull();
	}

}
