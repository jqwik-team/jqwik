package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
class ZoneOffsetTests {

	@Provide
	Arbitrary<ZoneOffset> offsets() {
		return Times.zoneOffsets();
	}

	@Property
	void validZoneOffsetIsGenerated(@ForAll("offsets") ZoneOffset offset) {
		assertThat(offset).isNotNull();
	}

	@Property
	@Disabled("Not available at the moment")
	void validZoneOffsetIsGeneratedWithAnnotation(@ForAll ZoneOffset offset) {
		assertThat(offset).isNotNull();
	}

	@Group
	class CheckOffsetMethods {

		@Group
		class OffsetMethods {

			@Property
			void atTheEarliest(@ForAll("offsets") ZoneOffset startOffset, @ForAll Random random) {

				Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().atTheEarliest(startOffset);

				assertAllGenerated(offsets.generator(1000), random, offset -> {
					assertThat(offset.getTotalSeconds()).isGreaterThanOrEqualTo(startOffset.getTotalSeconds());
					return true;
				});

			}

			@Property
			void atTheLatest(@ForAll("offsets") ZoneOffset endOffset, @ForAll Random random) {

				Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().atTheLatest(endOffset);

				assertAllGenerated(offsets.generator(1000), random, offset -> {
					assertThat(offset.getTotalSeconds()).isLessThanOrEqualTo(endOffset.getTotalSeconds());
					return true;
				});

			}

			@Property
			void between(@ForAll("offsets") ZoneOffset startOffset, @ForAll("offsets") ZoneOffset endOffset, @ForAll Random random) {

				Assume.that(startOffset.getTotalSeconds() <= endOffset.getTotalSeconds());

				Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().between(startOffset, endOffset);

				assertAllGenerated(offsets.generator(1000), random, offset -> {
					assertThat(offset.getTotalSeconds()).isGreaterThanOrEqualTo(startOffset.getTotalSeconds());
					assertThat(offset.getTotalSeconds()).isLessThanOrEqualTo(endOffset.getTotalSeconds());
					return true;
				});
			}

			@Property
			void betweenSame(@ForAll("offsets") ZoneOffset sameOffset, @ForAll Random random) {

				Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().between(sameOffset, sameOffset);

				assertAllGenerated(offsets.generator(1000), random, offset -> {
					assertThat(offset).isEqualTo(sameOffset);
					return true;
				});

			}

		}

		@Group
		class HourMethods {

			@Property
			void hourBetween(@ForAll("hours") int startHour, @ForAll("hours") int endHour, @ForAll Random random) {

				Assume.that(startHour <= endHour);

				Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().hourBetween(startHour, endHour);

				assertAllGenerated(offsets.generator(1000), random, offset -> {
					assertThat(offset.getTotalSeconds() / 3600).isGreaterThanOrEqualTo(startHour);
					assertThat(offset.getTotalSeconds() / 3600).isLessThanOrEqualTo(endHour);
					return true;
				});

			}

			@Property
			void hourBetweenSame(@ForAll("hours") int hour, @ForAll Random random) {

				Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().hourBetween(hour, hour);

				assertAllGenerated(offsets.generator(1000), random, offset -> {
					assertThat(offset.getTotalSeconds() / 3600).isEqualTo(hour);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> hours() {
				return Arbitraries.integers().between(-18, 18);
			}

		}

		@Group
		class MinuteMethods {

			@Property
			void minuteBetween(@ForAll("minutes") int startMinute, @ForAll("minutes") int endMinute, @ForAll Random random) {

				Assume.that(startMinute <= endMinute);

				Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().minuteBetween(startMinute, endMinute);

				assertAllGenerated(offsets.generator(1000), random, offset -> {
					assertThat(Math.abs((offset.getTotalSeconds() % 3600) / 60)).isGreaterThanOrEqualTo(startMinute);
					assertThat(Math.abs((offset.getTotalSeconds() % 3600) / 60)).isLessThanOrEqualTo(endMinute);
					return true;
				});

			}

			@Property
			void minuteBetweenSame(@ForAll("minutes") int minute, @ForAll Random random) {

				Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().minuteBetween(minute, minute);

				assertAllGenerated(offsets.generator(1000), random, offset -> {
					assertThat(Math.abs((offset.getTotalSeconds() % 3600) / 60)).isEqualTo(minute);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> minutes() {
				return Arbitraries.integers().between(0, 59);
			}

		}

		@Group
		class SecondMethods {

			@Property
			void secondBetween(@ForAll("seconds") int startSecond, @ForAll("seconds") int endSecond, @ForAll Random random) {

				Assume.that(startSecond <= endSecond);

				Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().secondBetween(startSecond, endSecond);

				assertAllGenerated(offsets.generator(1000), random, offset -> {
					assertThat(Math.abs(offset.getTotalSeconds() % 60)).isGreaterThanOrEqualTo(startSecond);
					assertThat(Math.abs(offset.getTotalSeconds() % 60)).isLessThanOrEqualTo(endSecond);
					return true;
				});

			}

			@Property
			void secondBetweenSame(@ForAll("seconds") int second, @ForAll Random random) {

				Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().secondBetween(second, second);

				assertAllGenerated(offsets.generator(1000), random, offset -> {
					assertThat(Math.abs(offset.getTotalSeconds() % 60)).isEqualTo(second);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> seconds() {
				return Arbitraries.integers().between(0, 59);
			}

		}

	}

	@Group
	class CheckEqualDistribution {

		@Property
		void minusAndPlusIsPossibleWhenHourIsZero(@ForAll("offsetsNear0") ZoneOffset offset) {
			int totalSeconds = offset.getTotalSeconds();
			Assume.that(totalSeconds > -3600 && totalSeconds < 3600 && totalSeconds != 0);
			Statistics.label("Negative value with Hour is zero")
					  .collect(totalSeconds < 0)
					  .coverage(coverage -> {
						  coverage.check(true).percentage(p -> p >= 35);
						  coverage.check(false).percentage(p -> p >= 35);
					  });
		}

		@Provide
		Arbitrary<ZoneOffset> offsetsNear0() {
			return Times.zoneOffsets().hourBetween(-2, 2);
		}

	}

}
