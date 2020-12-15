package net.jqwik.api.time;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.testing.TestingSupport.*;

@Group
class DaysOfMonthTests {

	@Property
	void validDayOfWeekIsGenerated(@ForAll("dayOfMonth") int dayOfMonth) {
		assertThat(dayOfMonth).isGreaterThanOrEqualTo(1);
		assertThat(dayOfMonth).isLessThanOrEqualTo(31);
	}

	@Provide
	Arbitrary<Integer> dayOfMonth() {
		return Dates.daysOfMonth();
	}

	@Group
	class CheckDaysOfMonthMethods {

		@Property
		void greaterOrEqual(@ForAll("dayOfMonth") int dayOfMonth, @ForAll Random random) {

			Arbitrary<Integer> dayOfMonths = Dates.daysOfMonth().greaterOrEqual(dayOfMonth);

			assertAllGenerated(dayOfMonths.generator(1000), random, d -> {
				assertThat(d).isGreaterThanOrEqualTo(dayOfMonth);
				return true;
			});

		}

		@Property
		void lessOrEqual(@ForAll("dayOfMonth") int dayOfMonth, @ForAll Random random) {

			Arbitrary<Integer> dayOfMonths = Dates.daysOfMonth().lessOrEqual(dayOfMonth);

			assertAllGenerated(dayOfMonths.generator(1000), random, d -> {
				assertThat(d).isLessThanOrEqualTo(dayOfMonth);
				return true;
			});

		}

		@Property
		void between(@ForAll("dayOfMonth") int startDayOfMonth, @ForAll("dayOfMonth") int endDayOfMonth, @ForAll Random random) {

			Assume.that(startDayOfMonth < endDayOfMonth);

			Arbitrary<Integer> dayOfMonths = Dates.daysOfMonth().between(startDayOfMonth, endDayOfMonth);

			assertAllGenerated(dayOfMonths.generator(1000), random, year -> {
				assertThat(year).isGreaterThanOrEqualTo(startDayOfMonth);
				assertThat(year).isLessThanOrEqualTo(endDayOfMonth);
				return true;
			});

		}

		@Property
		void betweenSame(@ForAll("dayOfMonth") int dayOfMonth, @ForAll Random random) {

			Arbitrary<Integer> dayOfMonths = Dates.daysOfMonth().between(dayOfMonth, dayOfMonth);

			assertAllGenerated(dayOfMonths.generator(1000), random, d -> {
				assertThat(d).isEqualTo(dayOfMonth);
				return true;
			});

		}

	}

	@Group
	class Shrinking {

		@Property
		void defaultShrinking(@ForAll Random random) {
			DaysOfMonthArbitrary daysOfMonth = Dates.daysOfMonth();
			int value = shrinkToMinimal(daysOfMonth, random);
			assertThat(value).isEqualTo(1);
		}

		@Property
		void shrinksToSmallestFailingValue(@ForAll Random random){
			DaysOfMonthArbitrary daysOfMonths = Dates.daysOfMonth();
			TestingFalsifier<Integer> falsifier = day -> day.intValue() < 17;
			int value = shrinkToMinimal(daysOfMonths, random, falsifier);
			assertThat(value).isEqualTo(17);
		}

	}

	@Group
	class ExhaustiveGeneration {

		@Property(tries = 5)
		void between() {
			Optional<ExhaustiveGenerator<Integer>> optionalGenerator = Dates.daysOfMonth().between(10, 17).exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(8);
			assertThat(generator)
					.containsExactly(10, 11, 12, 13, 14, 15, 16, 17);
		}

	}

}
