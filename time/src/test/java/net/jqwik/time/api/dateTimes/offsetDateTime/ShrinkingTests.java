package net.jqwik.time.api.dateTimes.offsetDateTime;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

public class ShrinkingTests {

	@Property
	void defaultShrinking(@ForAll Random random) {
		OffsetDateTimeArbitrary dateTimes = DateTimes.offsetDateTimes();
		OffsetDateTime value = falsifyThenShrink(dateTimes, random);
		assertThat(value).isEqualTo(OffsetDateTime.of(LocalDateTime.of(1900, JANUARY, 1, 0, 0, 0), ZoneOffset.UTC));
	}

	@Property(tries = 40)
	void shrinksToSmallestFailingValue(@ForAll Random random) {
		OffsetDateTimeArbitrary dateTimes = DateTimes.offsetDateTimes();
		TestingFalsifier<OffsetDateTime> falsifier =
			dateTime -> dateTime.isBefore(OffsetDateTime.of(LocalDateTime.of(2013, MAY, 25, 13, 12, 55), ZoneOffset.ofHours(4)));
		OffsetDateTime value = falsifyThenShrink(dateTimes, random, falsifier);
		assertThat(value).isEqualTo(OffsetDateTime.of(LocalDateTime.of(2013, MAY, 25, 13, 12, 55), ZoneOffset.ofHours(4)));
	}

}
