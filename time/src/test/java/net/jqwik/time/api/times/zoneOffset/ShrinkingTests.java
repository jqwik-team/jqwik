package net.jqwik.time.api.times.zoneOffset;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

public class ShrinkingTests {

	@Property
	void defaultShrinking(@ForAll JqwikRandom random) {
		ZoneOffsetArbitrary offsets = Times.zoneOffsets();
		ZoneOffset value = falsifyThenShrink(offsets, random);
		assertThat(value).isEqualTo(ZoneOffset.of("Z"));
	}

	@Property
	void shrinksToSmallestFailingPositiveValue(@ForAll JqwikRandom random) {
		ZoneOffsetArbitrary offsets = Times.zoneOffsets();
		TestingFalsifier<ZoneOffset> falsifier = offset -> offset.getTotalSeconds() < ZoneOffset.ofHoursMinutesSeconds(2, 14, 33)
																								.getTotalSeconds();
		ZoneOffset value = falsifyThenShrink(offsets, random, falsifier);
		assertThat(value).isEqualTo(ZoneOffset.ofHoursMinutesSeconds(2, 15, 0));
	}

	@Property
	void shrinksToSmallestFailingNegativeValue(@ForAll JqwikRandom random) {
		ZoneOffsetArbitrary offsets = Times.zoneOffsets();
		TestingFalsifier<ZoneOffset> falsifier = offset -> offset.getTotalSeconds() > ZoneOffset.ofHoursMinutesSeconds(-2, -14, -33)
																								.getTotalSeconds();
		ZoneOffset value = falsifyThenShrink(offsets, random, falsifier);
		assertThat(value).isEqualTo(ZoneOffset.ofHoursMinutesSeconds(-2, -15, 0));
	}

}
