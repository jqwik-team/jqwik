package net.jqwik.api.time;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.time.copy.ArbitraryTestHelper.*;

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
		void greaterOrEqual(@ForAll("dayOfMonth") int dayOfMonth) {

			Arbitrary<Integer> dayOfMonths = Dates.daysOfMonth().greaterOrEqual(dayOfMonth);

			assertAllGenerated(dayOfMonths.generator(1000), d -> {
				assertThat(d).isGreaterThanOrEqualTo(dayOfMonth);
			});

		}

		@Property
		void lessOrEqual(@ForAll("dayOfMonth") int dayOfMonth) {

			Arbitrary<Integer> dayOfMonths = Dates.daysOfMonth().lessOrEqual(dayOfMonth);

			assertAllGenerated(dayOfMonths.generator(1000), d -> {
				assertThat(d).isLessThanOrEqualTo(dayOfMonth);
			});

		}

		@Property
		void between(@ForAll("dayOfMonth") int startDayOfMonth, @ForAll("dayOfMonth") int endDayOfMonth) {

			Assume.that(startDayOfMonth < endDayOfMonth);

			Arbitrary<Integer> dayOfMonths = Dates.daysOfMonth().between(startDayOfMonth, endDayOfMonth);

			assertAllGenerated(dayOfMonths.generator(1000), year -> {
				assertThat(year).isGreaterThanOrEqualTo(startDayOfMonth);
				assertThat(year).isLessThanOrEqualTo(endDayOfMonth);
			});

		}

		@Property
		void betweenSame(@ForAll("dayOfMonth") int dayOfMonth) {

			Arbitrary<Integer> dayOfMonths = Dates.daysOfMonth().between(dayOfMonth, dayOfMonth);

			assertAllGenerated(dayOfMonths.generator(1000), d -> {
				assertThat(d).isEqualTo(dayOfMonth);
			});

		}

	}

}
