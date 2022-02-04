package net.jqwik.docs.time;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

class DateTimeExamples {

	@Property(tries = 10)
	void calendarsFromInstants(@ForAll("calendars") Calendar cal) {
		System.out.println(cal.getTime());
	}

	@Provide
	Arbitrary<Calendar> calendars() {
		return DateTimes.instants().map(instant -> {
			ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
			return GregorianCalendar.from(zdt);
		});
	}

}
