package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class ZoneOffsetTests {

	@Provide
	Arbitrary<ZoneOffset> offsets() {
		return Times.zoneOffsets();
	}

	@Group
	class SimpleArbitraries {

		@Property
		void validZoneOffsetIsGenerated(@ForAll("offsets") ZoneOffset offset) {
			assertThat(offset).isNotNull();
		}

		@Property
		void onlyValidHoursAreGenerated(@ForAll("offsets") ZoneOffset offset) {
			ZoneOffset offsetStart = ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0);
			ZoneOffset offsetEnd = ZoneOffset.ofHoursMinutesSeconds(14, 0, 0);
			assertThat(offset.getTotalSeconds()).isGreaterThanOrEqualTo(offsetStart.getTotalSeconds());
			assertThat(offset.getTotalSeconds()).isLessThanOrEqualTo(offsetEnd.getTotalSeconds());
		}

		@Property
		void onlyValidMinutesAreGenerated(@ForAll("offsets") ZoneOffset offset) {
			int minutes = Math.abs((offset.getTotalSeconds() % 3600) / 60);
			assertThat(minutes % 15).isEqualTo(0);
		}

		@Property
		void onlyValidSecondsAreGenerated(@ForAll("offsets") ZoneOffset offset) {
			int seconds = Math.abs(offset.getTotalSeconds() % 60);
			assertThat(seconds).isEqualTo(0);
		}

	}

	@Group
	class DefaultGeneration {

		@Property
		void validZoneOffsetIsGenerated(@ForAll ZoneOffset offset) {
			assertThat(offset).isNotNull();
		}

	}

	@Group
	class CheckOffsetMethods {

		@Group
		class OffsetMethods {

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

			@Property(shrinking = ShrinkingMode.OFF)
			@ExpectFailure(failureType = IllegalArgumentException.class)
			void betweenNotGeneratedValues(
					@ForAll("times") LocalTime start,
					@ForAll("times") LocalTime end,
					@ForAll boolean startValueIsPositive,
					@ForAll boolean endValueIsPositive,
					@ForAll Random random
			) {

				Assume.that(startValueIsPositive || !start.isAfter(LocalTime.of(12, 0, 0)));
				Assume.that(endValueIsPositive || !end.isAfter(LocalTime.of(12, 0, 0)));
				int mul = startValueIsPositive ? 1 : -1;
				ZoneOffset startOffset = ZoneOffset
												 .ofHoursMinutesSeconds(mul * start.getHour(), mul * start.getMinute(), mul * start.getSecond());
				mul = endValueIsPositive ? 1 : -1;
				ZoneOffset endOffset = ZoneOffset.ofHoursMinutesSeconds(mul * end.getHour(), mul * end.getMinute(), mul * end.getSecond());

				Assume.that(startOffset.getTotalSeconds() <= endOffset.getTotalSeconds());

				Arbitrary<ZoneOffset> offsets = Times.zoneOffsets().between(startOffset, endOffset);

				assertAllGenerated(offsets.generator(1000), random, offset -> {
					assertThat(offset.getTotalSeconds()).isGreaterThanOrEqualTo(startOffset.getTotalSeconds());
					assertThat(offset.getTotalSeconds()).isLessThanOrEqualTo(endOffset.getTotalSeconds());
					return true;
				});

			}

			@Provide
			Arbitrary<LocalTime> times() {
				return Times.times().atTheLatest(LocalTime.of(14, 0, 0));
			}

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			ZoneOffsetArbitrary offsets = Times.zoneOffsets();
			ZoneOffset value = falsifyThenShrink(offsets, random);
			assertThat(value).isEqualTo(ZoneOffset.of("Z"));
		}

		@Property
		void shrinksToSmallestFailingPositiveValue(@ForAll Random random) {
			ZoneOffsetArbitrary offsets = Times.zoneOffsets();
			TestingFalsifier<ZoneOffset> falsifier = offset -> offset.getTotalSeconds() < ZoneOffset.ofHoursMinutesSeconds(2, 14, 33)
																									.getTotalSeconds();
			ZoneOffset value = falsifyThenShrink(offsets, random, falsifier);
			assertThat(value).isEqualTo(ZoneOffset.ofHoursMinutesSeconds(2, 15, 0));
		}

		@Property
		void shrinksToSmallestFailingNegativeValue(@ForAll Random random) {
			ZoneOffsetArbitrary offsets = Times.zoneOffsets();
			TestingFalsifier<ZoneOffset> falsifier = offset -> offset.getTotalSeconds() > ZoneOffset.ofHoursMinutesSeconds(-2, -14, -33)
																									.getTotalSeconds();
			ZoneOffset value = falsifyThenShrink(offsets, random, falsifier);
			assertThat(value).isEqualTo(ZoneOffset.ofHoursMinutesSeconds(-2, -15, 0));
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void between() {
			Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
					Times.zoneOffsets()
						 .between(
								 ZoneOffset.ofHoursMinutesSeconds(11, 11, 23),
								 ZoneOffset.ofHoursMinutesSeconds(12, 19, 2)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(5);
			assertThat(generator).containsExactly(
					ZoneOffset.ofHoursMinutesSeconds(11, 15, 0),
					ZoneOffset.ofHoursMinutesSeconds(11, 30, 0),
					ZoneOffset.ofHoursMinutesSeconds(11, 45, 0),
					ZoneOffset.ofHoursMinutesSeconds(12, 0, 0),
					ZoneOffset.ofHoursMinutesSeconds(12, 15, 0)
			);
		}

		@Example
		void between2() {
			Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
					Times.zoneOffsets()
						 .between(
								 ZoneOffset.ofHoursMinutesSeconds(-9, -14, -59),
								 ZoneOffset.ofHoursMinutesSeconds(-8, -15, -11)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly(
					ZoneOffset.ofHoursMinutesSeconds(-9, 0, 0),
					ZoneOffset.ofHoursMinutesSeconds(-8, -45, 0),
					ZoneOffset.ofHoursMinutesSeconds(-8, -30, 0)
			);
		}

		@Example
		void edgeCase1() {
			Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
					Times.zoneOffsets()
						 .between(
								 ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0),
								 ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(1);
			assertThat(generator).containsExactly(
					ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0)
			);
		}

		@Example
		void edgeCase2() {
			Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
					Times.zoneOffsets()
						 .between(
								 ZoneOffset.ofHoursMinutesSeconds(14, 0, 0),
								 ZoneOffset.ofHoursMinutesSeconds(14, 0, 0)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(1);
			assertThat(generator).containsExactly(
					ZoneOffset.ofHoursMinutesSeconds(14, 0, 0)
			);
		}

		@Example
		void edgeCase3() {
			Optional<ExhaustiveGenerator<ZoneOffset>> optionalGenerator =
					Times.zoneOffsets()
						 .between(
								 ZoneOffset.ofHoursMinutesSeconds(0, 0, 0),
								 ZoneOffset.ofHoursMinutesSeconds(0, 0, 0)
						 )
						 .exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<ZoneOffset> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(1);
			assertThat(generator).containsExactly(
					ZoneOffset.ofHoursMinutesSeconds(0, 0, 0)
			);
		}

	}

	@Group
	class EdgeCasesTests {

		@Example
		void all() {
			ZoneOffsetArbitrary offsets = Times.zoneOffsets();
			Set<ZoneOffset> edgeCases = collectEdgeCaseValues(offsets.edgeCases());
			assertThat(edgeCases).hasSize(3);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					ZoneOffset.ofHoursMinutesSeconds(-12, 0, 0),
					ZoneOffset.of("Z"),
					ZoneOffset.ofHoursMinutesSeconds(14, 0, 0)
			);
		}

		@Example
		void betweenPositive() {
			ZoneOffsetArbitrary offsets =
					Times.zoneOffsets()
						 .between(ZoneOffset.ofHoursMinutesSeconds(11, 23, 21), ZoneOffset.ofHoursMinutesSeconds(13, 29, 59));
			Set<ZoneOffset> edgeCases = collectEdgeCaseValues(offsets.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					ZoneOffset.ofHoursMinutesSeconds(11, 30, 0),
					ZoneOffset.ofHoursMinutesSeconds(13, 15, 0)
			);
		}

		@Example
		void betweenNegative() {
			ZoneOffsetArbitrary offsets =
					Times.zoneOffsets()
						 .between(ZoneOffset.ofHoursMinutesSeconds(-11, -15, -19), ZoneOffset.ofHoursMinutesSeconds(-10, -23, -21));
			Set<ZoneOffset> edgeCases = collectEdgeCaseValues(offsets.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					ZoneOffset.ofHoursMinutesSeconds(-11, -15, 0),
					ZoneOffset.ofHoursMinutesSeconds(-10, -30, 0)
			);
		}

	}

	@Group
	class CheckEqualDistribution {

		@Property
		void negativeAndPositiveValuesAreGenerated(@ForAll("offsets") ZoneOffset offset) {
			int totalSeconds = offset.getTotalSeconds();
			Assume.that(totalSeconds != 0);
			Statistics.label("Negative value")
					  .collect(totalSeconds < 0)
					  .coverage(this::check5050BooleanCoverage);
		}

		@Property
		void valueZeroIsGenerated(@ForAll("offsets") ZoneOffset offset) {
			Statistics.label("00:00:00 is possible")
					  .collect(offset.getTotalSeconds() == 0)
					  .coverage(coverage -> {
						  coverage.check(true).count(c -> c >= 1);
					  });
		}

		@Property
		void minusAndPlusIsPossibleWhenHourIsZero(@ForAll("offsetsNear0") ZoneOffset offset) {
			int totalSeconds = offset.getTotalSeconds();
			Assume.that(totalSeconds > -3600 && totalSeconds < 3600 && totalSeconds != 0);
			Statistics.label("Negative value with Hour is zero")
					  .collect(totalSeconds < 0)
					  .coverage(this::check5050BooleanCoverage);
		}

		@Property
		void hours(@ForAll("offsets") ZoneOffset offset) {
			Statistics.label("Hours")
					  .collect(offset.getTotalSeconds() / 3600)
					  .coverage(this::checkHourCoverage);
		}

		@Property
		void minutes(@ForAll("offsets") ZoneOffset offset) {
			Statistics.label("Minutes")
					  .collect(Math.abs((offset.getTotalSeconds() % 3600) / 60))
					  .coverage(this::checkMinuteCoverage);
		}

		@Provide
		Arbitrary<ZoneOffset> offsetsNear0() {
			return Times.zoneOffsets().between(ZoneOffset.ofHoursMinutesSeconds(-1, 0, 0), ZoneOffset.ofHoursMinutesSeconds(1, 0, 0));
		}

		private void check5050BooleanCoverage(StatisticsCoverage coverage) {
			coverage.check(true).percentage(p -> p >= 35);
			coverage.check(false).percentage(p -> p >= 35);
		}

		private void checkHourCoverage(StatisticsCoverage coverage) {
			coverage.check(-12).percentage(p -> p >= 0.8);
			for (int value = -11; value <= 13; value++) {
				coverage.check(value).percentage(p -> p >= 3);
			}
			coverage.check(14).percentage(p -> p >= 0.8);
		}

		private void checkMinuteCoverage(StatisticsCoverage coverage) {
			for (int value = 0; value < 60; value += 15) {
				coverage.check(value).percentage(p -> p >= 20);
			}
		}

	}

	@Group
	class InvalidConfigurations {

		@Example
		void minOffsetLessThanMinimumOffset() {
			assertThatThrownBy(
					() -> Times.zoneOffsets().between(ZoneOffset.ofHoursMinutesSeconds(-12, 0, -1), ZoneOffset.ofHours(1))
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void minOffsetGreaterThanMaximumOffset() {
			assertThatThrownBy(
					() -> Times.zoneOffsets().between(ZoneOffset.ofHoursMinutesSeconds(14, 0, 1), ZoneOffset.ofHours(15))
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void maxOffsetGreaterThanMaximumOffset() {
			assertThatThrownBy(
					() -> Times.zoneOffsets().between(ZoneOffset.ofHours(1), ZoneOffset.ofHoursMinutesSeconds(14, 0, 1))
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void noValuesCanBeGeneratedNegative() {
			assertThatThrownBy(
					() -> Times.zoneOffsets()
							   .between(ZoneOffset.ofHoursMinutesSeconds(-11, -9, -4), ZoneOffset.ofHoursMinutesSeconds(-11, -9, -3))
							   .generator(1000)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Example
		void noValuesCanBeGeneratedPositive() {
			assertThatThrownBy(
					() -> Times.zoneOffsets()
							   .between(ZoneOffset.ofHoursMinutesSeconds(11, 9, 3), ZoneOffset.ofHoursMinutesSeconds(11, 9, 4))
							   .generator(1000)
			).isInstanceOf(IllegalArgumentException.class);
		}

	}

}