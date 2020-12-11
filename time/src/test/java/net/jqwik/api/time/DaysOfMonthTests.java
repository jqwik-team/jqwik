package net.jqwik.api.time;

import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.testing.TestingSupport.*;

@Group
public class DaysOfMonthTests {

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

}
