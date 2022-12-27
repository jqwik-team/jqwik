package net.jqwik.time.api.dateTimes.zonedDateTime;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

@PropertyDefaults(tries = 40)
public class ShrinkingTests {

	@Property
	void defaultShrinking(@ForAll JqwikRandom random) {
		ZonedDateTimeArbitrary dateTimes = DateTimes.zonedDateTimes();
		ZonedDateTime value = falsifyThenShrink(dateTimes, random);
		assertThat(value).isEqualTo(ZonedDateTime.of(1900, 1, 1, 0, 0, 0, 0, ZoneId.of("Etc/GMT-14")));
	}

	@Property
	void shrinksToSmallestFailingValue(@ForAll JqwikRandom random) {
		ZonedDateTimeArbitrary dateTimes = DateTimes.zonedDateTimes();
		TestingFalsifier<ZonedDateTime> falsifier =
			dateTime -> dateTime.isBefore(ZonedDateTime.of(LocalDateTime.of(2013, MAY, 25, 13, 12, 55), ZoneId.of("Indian/Reunion")));
		ZonedDateTime value = falsifyThenShrink(dateTimes, random, falsifier);
		assertThat(value).isIn(ZonedDateTime.of(LocalDateTime.of(2013, MAY, 25, 13, 12, 55), ZoneId.of("Indian/Reunion")));
	}

}
