package net.jqwik.time.api;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;
import net.jqwik.time.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class LocalTimeTests {

	@Provide
	Arbitrary<LocalTime> times() {
		return Times.times();
	}

	@Property
	void validLocalTimeIsGenerated(@ForAll("times") LocalTime time) {
		assertThat(time).isNotNull();
	}

	@Property
	void validLocalTimeIsGeneratedWithAnnotation(@ForAll LocalTime time) {
		assertThat(time).isNotNull();
	}

	@Group
	class CheckTimeMethods {

		@Group
		class TimeMethods {

			@Property
			void atTheEarliest(@ForAll("times") LocalTime startTime, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().atTheEarliest(startTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isAfterOrEqualTo(startTime);
					return true;
				});

			}

			@Property
			void atTheLatest(@ForAll("times") LocalTime endTime, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().atTheLatest(endTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isBeforeOrEqualTo(endTime);
					return true;
				});

			}

			@Property
			void between(@ForAll("times") LocalTime startTime, @ForAll("times") LocalTime endTime, @ForAll Random random) {

				Assume.that(!startTime.isAfter(endTime));

				Arbitrary<LocalTime> times = Times.times().between(startTime, endTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isAfterOrEqualTo(startTime);
					assertThat(time).isBeforeOrEqualTo(endTime);
					return true;
				});
			}

			@Property
			void betweenSame(@ForAll("times") LocalTime sameTime, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().between(sameTime, sameTime);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time).isEqualTo(sameTime);
					return true;
				});

			}

		}

		@Group
		class HourMethods {

			@Property
			void hourBetween(@ForAll("hours") int startHour, @ForAll("hours") int endHour, @ForAll Random random) {

				Assume.that(startHour <= endHour);

				Arbitrary<LocalTime> times = Times.times().hourBetween(startHour, endHour);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getHour()).isGreaterThanOrEqualTo(startHour);
					assertThat(time.getHour()).isLessThanOrEqualTo(endHour);
					return true;
				});

			}

			@Property
			void hourBetweenSame(@ForAll("hours") int hour, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().hourBetween(hour, hour);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getHour()).isEqualTo(hour);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> hours() {
				return Arbitraries.integers().between(0, 23);
			}

		}

		@Group
		class MinuteMethods {

			@Property
			void minuteBetween(@ForAll("minutes") int startMinute, @ForAll("minutes") int endMinute, @ForAll Random random) {

				Assume.that(startMinute <= endMinute);

				Arbitrary<LocalTime> times = Times.times().minuteBetween(startMinute, endMinute);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getMinute()).isGreaterThanOrEqualTo(startMinute);
					assertThat(time.getMinute()).isLessThanOrEqualTo(endMinute);
					return true;
				});

			}

			@Property
			void minuteBetweenSame(@ForAll("minutes") int minute, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().minuteBetween(minute, minute);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getMinute()).isEqualTo(minute);
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

				Arbitrary<LocalTime> times = Times.times().secondBetween(startSecond, endSecond);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getSecond()).isGreaterThanOrEqualTo(startSecond);
					assertThat(time.getSecond()).isLessThanOrEqualTo(endSecond);
					return true;
				});

			}

			@Property
			void secondBetweenSame(@ForAll("seconds") int second, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().secondBetween(second, second);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getSecond()).isEqualTo(second);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> seconds() {
				return Arbitraries.integers().between(0, 59);
			}

		}

		@Group
		class MillisecondMethods {

			@Property
			void millisecondBetween(
					@ForAll("milliseconds") int startMillisecond,
					@ForAll("milliseconds") int endMillisecond,
					@ForAll Random random
			) {

				Assume.that(startMillisecond <= endMillisecond);

				Arbitrary<LocalTime> times = Times.times().millisecondBetween(startMillisecond, endMillisecond);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getNano()).isGreaterThanOrEqualTo(startMillisecond * 1_000_000);
					assertThat(time.getNano()).isLessThanOrEqualTo(endMillisecond * 1_000_000 + 999_999);
					return true;
				});

			}

			@Property
			void millisecondBetweenSame(@ForAll("milliseconds") int millisecond, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().millisecondBetween(millisecond, millisecond);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getNano()).isGreaterThanOrEqualTo(millisecond * 1_000_000);
					assertThat(time.getNano()).isLessThanOrEqualTo(millisecond * 1_000_000 + 999_999);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> microseconds() {
				return Arbitraries.integers().between(0, 999);
			}

		}

		@Group
		class MicrosecondMethods {

			@Property
			void microsecondBetween(
					@ForAll("microseconds") int startMicrosecond,
					@ForAll("microseconds") int endMicrosecond,
					@ForAll Random random
			) {

				Assume.that(startMicrosecond <= endMicrosecond);

				Arbitrary<LocalTime> times = Times.times().microsecondBetween(startMicrosecond, endMicrosecond);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getNano() % 1_000_000).isGreaterThanOrEqualTo(startMicrosecond * 1000);
					assertThat(time.getNano() % 1_000_000).isLessThanOrEqualTo(endMicrosecond * 1000 + 999);
					return true;
				});

			}

			@Property
			void microsecondBetweenSame(@ForAll("microseconds") int microsecond, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().microsecondBetween(microsecond, microsecond);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getNano() % 1_000_000).isGreaterThanOrEqualTo(microsecond * 1000);
					assertThat(time.getNano() % 1_000_000).isLessThanOrEqualTo(microsecond * 1000 + 999);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> microseconds() {
				return Arbitraries.integers().between(0, 999);
			}

		}

		@Group
		class NanosecondMethods {

			@Property
			void nanosecondBetween(
					@ForAll("nanoseconds") int startNanosecond,
					@ForAll("nanoseconds") int endNanosecond,
					@ForAll Random random
			) {

				Assume.that(startNanosecond <= endNanosecond);

				Arbitrary<LocalTime> times = Times.times().nanosecondBetween(startNanosecond, endNanosecond);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getNano() % 1000).isGreaterThanOrEqualTo(startNanosecond);
					assertThat(time.getNano() % 1000).isLessThanOrEqualTo(endNanosecond);
					return true;
				});

			}

			@Property
			void nanosecondBetweenSame(@ForAll("nanoseconds") int nanosecond, @ForAll Random random) {

				Arbitrary<LocalTime> times = Times.times().nanosecondBetween(nanosecond, nanosecond);

				assertAllGenerated(times.generator(1000), random, time -> {
					assertThat(time.getNano() % 1000).isEqualTo(nanosecond);
					return true;
				});

			}

			@Provide
			Arbitrary<Integer> nanoseconds() {
				return Arbitraries.integers().between(0, 999);
			}

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			LocalTimeArbitrary times = Times.times();
			LocalTime value = falsifyThenShrink(times, random);
			assertThat(value).isEqualTo(LocalTime.of(0, 0, 0, 0));
		}

		@Property
		void shrinksToSmallestFailingValue(@ForAll Random random) {
			LocalTimeArbitrary times = Times.times();
			TestingFalsifier<LocalTime> falsifier = time -> time.isBefore(LocalTime.of(9, 13, 42, 143_921_111));
			LocalTime value = falsifyThenShrink(times, random, falsifier);
			assertThat(value).isEqualTo(LocalTime.of(9, 13, 42, 143_921_111));
		}

	}

	@Group
	class ExhaustiveGeneration {

		//TODO

	}

	@Group
	class EdgeCasesTests {

		@Example
		void all() {
			LocalTimeArbitrary times = Times.times();
			Set<LocalTime> edgeCases = collectEdgeCases(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					LocalTime.of(0, 0, 0, 0),
					LocalTime.of(23, 59, 59, 999_999_999)
			);
		}

		@Example
		void between() {
			LocalTimeArbitrary times =
					Times.times()
						 .between(LocalTime.of(11, 23, 21, 301_428_111), LocalTime.of(21, 15, 19, 199_321_789));
			Set<LocalTime> edgeCases = collectEdgeCases(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					LocalTime.of(11, 23, 21, 301_428_111),
					LocalTime.of(21, 15, 19, 199_321_789)
			);
		}

		@Example
		void betweenMicrosecond() {
			LocalTimeArbitrary times =
					Times.times()
						 .hourBetween(11, 12)
						 .minuteBetween(23, 31)
						 .secondBetween(5, 10)
						 .millisecondBetween(100, 200)
						 .microsecondBetween(567, 888);
			Set<LocalTime> edgeCases = collectEdgeCases(times.edgeCases());
			assertThat(edgeCases).hasSize(2);
			assertThat(edgeCases).containsExactlyInAnyOrder(
					LocalTime.of(11, 23, 5, 100_567_000),
					LocalTime.of(12, 31, 10, 200_888_999)
			);
		}

	}

	@Group
	class CheckEqualDistribution {

		//TODO

	}

	@Group
	class InvalidConfigurations {

		//TODO Maybe

	}

}
