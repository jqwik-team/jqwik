package net.jqwik.time.api.times.offsetTime;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

@PropertyDefaults(tries = 100)
public class ShrinkingTests {

	@Property
	void defaultShrinking(@ForAll JqwikRandom random) {
		OffsetTimeArbitrary times = Times.offsetTimes();
		OffsetTime value = falsifyThenShrink(times, random);
		assertThat(value).isEqualTo(OffsetTime.of(LocalTime.of(0, 0, 0), ZoneOffset.of("Z")));
	}

}
