package net.jqwik.time.api.times.offsetTime.timeMethods;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 100)
public class OffsetTests {

	@Property
	void between(@ForAll ZoneOffset startOffset, @ForAll ZoneOffset endOffset, @ForAll JqwikRandom random) {

		Assume.that(startOffset.getTotalSeconds() <= endOffset.getTotalSeconds());

		Arbitrary<OffsetTime> times = Times.offsetTimes().offsetBetween(startOffset, endOffset);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.getOffset().getTotalSeconds()).isGreaterThanOrEqualTo(startOffset.getTotalSeconds());
			assertThat(time.getOffset().getTotalSeconds()).isLessThanOrEqualTo(endOffset.getTotalSeconds());
			return true;
		});

	}

	@Property
	void betweenSame(@ForAll ZoneOffset offset, @ForAll JqwikRandom random) {

		Arbitrary<OffsetTime> times = Times.offsetTimes().offsetBetween(offset, offset);

		checkAllGenerated(times.generator(1000), random, time -> {
			assertThat(time.getOffset()).isEqualTo(offset);
			return true;
		});

	}

}
