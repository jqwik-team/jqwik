package net.jqwik.time.api.times.zoneOffset;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.time.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@PropertyDefaults(tries = 100)
public class OffsetMethodsTests {

	@Provide
	Arbitrary<ZoneOffset> offsets() {
		return Times.zoneOffsets();
	}

	@Property
	void between(@ForAll("offsets") ZoneOffset startOffset, @ForAll("offsets") ZoneOffset endOffset, @ForAll JqwikRandom random) {

		Assume.that(startOffset.getTotalSeconds() <= endOffset.getTotalSeconds());

		Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().between(startOffset, endOffset);

		checkAllGenerated(offsets.generator(1000), random, offset -> {
			assertThat(offset.getTotalSeconds()).isGreaterThanOrEqualTo(startOffset.getTotalSeconds());
			assertThat(offset.getTotalSeconds()).isLessThanOrEqualTo(endOffset.getTotalSeconds());
			return true;
		});
	}

	@Property
	void betweenSame(@ForAll("offsets") ZoneOffset sameOffset, @ForAll JqwikRandom random) {

		Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().between(sameOffset, sameOffset);

		checkAllGenerated(offsets.generator(1000), random, offset -> {
			assertThat(offset).isEqualTo(sameOffset);
			return true;
		});

	}

	@Property(shrinking = ShrinkingMode.OFF)
	void betweenNotGeneratedValues(
		@ForAll("times") LocalTime start,
		@ForAll("times") LocalTime end,
		@ForAll boolean startValueIsPositive,
		@ForAll boolean endValueIsPositive,
		@ForAll JqwikRandom random
	) {

		try {
			Assume.that(startValueIsPositive || !start.isAfter(LocalTime.of(12, 0, 0)));
			Assume.that(endValueIsPositive || !end.isAfter(LocalTime.of(12, 0, 0)));
			int mul = startValueIsPositive ? 1 : -1;
			ZoneOffset startOffset = ZoneOffset
										 .ofHoursMinutesSeconds(mul * start.getHour(), mul * start.getMinute(), mul * start.getSecond());
			mul = endValueIsPositive ? 1 : -1;
			ZoneOffset endOffset = ZoneOffset
									   .ofHoursMinutesSeconds(mul * end.getHour(), mul * end.getMinute(), mul * end.getSecond());

			Assume.that(startOffset.getTotalSeconds() <= endOffset.getTotalSeconds());

			Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().between(startOffset, endOffset);

			checkAllGenerated(offsets.generator(1000), random, offset -> {
				assertThat(offset.getTotalSeconds()).isGreaterThanOrEqualTo(startOffset.getTotalSeconds());
				assertThat(offset.getTotalSeconds()).isLessThanOrEqualTo(endOffset.getTotalSeconds());
				return true;
			});
		} catch (IllegalArgumentException e) {
			//do nothing
		}

	}

	@Provide
	Arbitrary<LocalTime> times() {
		return Times.times().atTheLatest(LocalTime.of(14, 0, 0));
	}

}
