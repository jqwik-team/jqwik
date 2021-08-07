package net.jqwik.time.api.dateTimes.instant;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static java.time.Month.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

public class ShrinkingTests {

	@Property
	void defaultShrinking(@ForAll Random random) {
		InstantArbitrary instants = DateTimes.instants();
		Instant value = falsifyThenShrink(instants, random);
		assertThat(value).isEqualTo(LocalDateTime.of(1900, JANUARY, 1, 0, 0, 0).toInstant(ZoneOffset.UTC));
	}

}
