package net.jqwik.api.time;

import java.time.*;

import net.jqwik.api.*;
import net.jqwik.time.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.time.copy.ArbitraryTestHelper.*;

@Group
class DatesTests {

	@Property
	void validLocalDateIsGenerated(@ForAll("dates") LocalDate localDate) {
		assertThat(localDate).isNotNull();
	}

	@Provide
	Arbitrary<LocalDate> dates() {
		return Dates.dates();
	}

	@Group
	class CheckDateMethods {

		@Group
		class DateMethods {

			@Property
			void atTheEarliest(@ForAll("dates") LocalDate startDate) {

				Arbitrary<LocalDate> dates = Dates.dates().atTheEarliest(startDate);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date).isBeforeOrEqualTo(startDate);
				});

			}

			@Property
			void atTheLatest(@ForAll("dates") LocalDate endDate) {

				Arbitrary<LocalDate> dates = Dates.dates().atTheLatest(endDate);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date).isAfterOrEqualTo(endDate);
				});

			}

			@Property
			void between(@ForAll("dates") LocalDate startDate, @ForAll("dates") LocalDate endDate) {

				Assume.that(!startDate.isAfter(endDate));

				Arbitrary<LocalDate> dates = Dates.dates().between(startDate, endDate);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date).isAfterOrEqualTo(startDate);
					assertThat(date).isBeforeOrEqualTo(endDate);
				});
			}

			@Property
			void betweenSame(@ForAll("dates") LocalDate sameDate) {

				Arbitrary<LocalDate> dates = Dates.dates().between(sameDate, sameDate);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date).isEqualTo(sameDate);
				});

			}

		}

		@Group
		class YearMethods {

			@Property
			void yearGreaterOrEqual(@ForAll("years") int year) {

				Arbitrary<LocalDate> dates = Dates.dates().yearGreaterOrEqual(year);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getYear()).isGreaterThanOrEqualTo(year);
				});

			}

			@Property
			void yearLessOrEqual(@ForAll("years") int year) {

				Arbitrary<LocalDate> dates = Dates.dates().yearLessOrEqual(year);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getYear()).isLessThanOrEqualTo(year);
				});

			}

			@Property
			void yearBetween(@ForAll("years") int startYear, @ForAll("years") int endYear) {

				Assume.that(startYear <= endYear);

				Arbitrary<LocalDate> dates = Dates.dates().yearBetween(startYear, endYear);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getYear()).isGreaterThanOrEqualTo(startYear);
					assertThat(date.getYear()).isLessThanOrEqualTo(endYear);
				});

			}

			@Property
			void yearBetweenSame(@ForAll("years") int year) {

				Arbitrary<LocalDate> dates = Dates.dates().yearBetween(year, year);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getYear()).isEqualTo(year);
				});

			}

			@Provide
			Arbitrary<Integer> years() {
				return Arbitraries.integers().between(LocalDate.MIN.getYear(), LocalDate.MAX.getYear());
			}

		}

		@Group
		class MonthMethods {

			@Property
			void monthGreaterOrEqual(@ForAll("months") int month) {

				Arbitrary<LocalDate> dates = Dates.dates().monthGreaterOrEqual(month);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getMonth()).isGreaterThanOrEqualTo(DefaultMonthArbitrary.getMonthFromInt(month));
				});

			}

			@Property
			void monthLessOrEqual(@ForAll("months") int month) {

				Arbitrary<LocalDate> dates = Dates.dates().monthLessOrEqual(month);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getMonth()).isLessThanOrEqualTo(DefaultMonthArbitrary.getMonthFromInt(month));
				});

			}

			@Property
			void monthBetween(@ForAll("months") int startMonth, @ForAll("months") int endMonth) {

				Assume.that(startMonth <= endMonth);

				Arbitrary<LocalDate> dates = Dates.dates().monthBetween(startMonth, endMonth);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getMonth()).isGreaterThanOrEqualTo(DefaultMonthArbitrary.getMonthFromInt(startMonth));
					assertThat(date.getMonth()).isLessThanOrEqualTo(DefaultMonthArbitrary.getMonthFromInt(endMonth));
				});

			}

			@Property
			void monthBetweenSame(@ForAll("months") int month) {

				Arbitrary<LocalDate> dates = Dates.dates().monthBetween(month, month);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getMonth()).isEqualTo(DefaultMonthArbitrary.getMonthFromInt(month));
				});

			}

			@Property
			void monthOnlyMonths(@ForAll("monthsOnlyMonths") Month[] months){

				Arbitrary<LocalDate> dates = Dates.dates().onlyMonths(months);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getMonth()).isIn(months);
				});

			}

			@Provide
			Arbitrary<Month[]> monthsOnlyMonths(){
				return MonthTests.generateMonths();
			}

			@Provide
			Arbitrary<Integer> months() {
				return Arbitraries.integers().between(1, 12);
			}

		}

		@Group
		class DayOfMonthMethods {

			@Property
			void dayOfMonthGreaterOrEqual(@ForAll("dayOfMonths") int dayOfMonth) {

				Arbitrary<LocalDate> dates = Dates.dates().dayOfMonthGreaterOrEqual(dayOfMonth);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getDayOfMonth()).isGreaterThanOrEqualTo(dayOfMonth);
				});

			}

			@Property
			void dayOfMonthLessOrEqual(@ForAll("dayOfMonths") int dayOfMonth) {

				Arbitrary<LocalDate> dates = Dates.dates().dayOfMonthLessOrEqual(dayOfMonth);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getDayOfMonth()).isLessThanOrEqualTo(dayOfMonth);
				});

			}

			@Property
			void dayOfMonthBetween(@ForAll("dayOfMonths") int startDayOfMonth, @ForAll("dayOfMonths") int endDayOfMonth) {

				Arbitrary<LocalDate> dates = Dates.dates().dayOfMonthBetween(startDayOfMonth, endDayOfMonth);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getDayOfMonth()).isGreaterThanOrEqualTo(startDayOfMonth);
					assertThat(date.getDayOfMonth()).isLessThanOrEqualTo(endDayOfMonth);
				});

			}

			@Property
			void dayOfMonthBetweenSame(@ForAll("dayOfMonths") int dayOfMonth) {

				Arbitrary<LocalDate> dates = Dates.dates().dayOfMonthBetween(dayOfMonth, dayOfMonth);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getDayOfMonth()).isEqualTo(dayOfMonth);
				});

			}

			@Provide
			Arbitrary<Integer> dayOfMonths() {
				return Arbitraries.integers().between(1, 31);
			}

		}

		@Group
		class OnlyDaysOfWeekMethods {

			@Property
			void onlyDaysOfWeek(@ForAll("onlyDayOfWeeks") DayOfWeek[] dayOfWeeks) {

				Arbitrary<LocalDate> dates = Dates.dates().onlyDaysOfWeek(dayOfWeeks);

				assertAllGenerated(dates.generator(1000), date -> {
					assertThat(date.getDayOfWeek()).isIn(dayOfWeeks);
				});
			}

			@Provide
			Arbitrary<DayOfWeek[]> onlyDayOfWeeks() {
				return DaysOfWeekTests.generateDayOfWeeks();
			}

		}

	}

}
