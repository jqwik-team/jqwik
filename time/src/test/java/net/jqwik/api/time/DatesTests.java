package net.jqwik.api.time;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

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

			private LocalDate startDate;
			private LocalDate endDate;

			@Property
			void atTheEarliest(@ForAll("datesAtTheEarliest") LocalDate date) {
				assertThat(date).isAfterOrEqualTo(startDate);
			}

			@Provide
			Arbitrary<LocalDate> datesAtTheEarliest() {
				startDate = Dates.dates().sample();
				return Dates.dates().atTheEarliest(startDate);
			}

			@Property
			void atTheLatest(@ForAll("datesAtTheLatest") LocalDate date) {
				assertThat(date).isBeforeOrEqualTo(endDate);
			}

			@Provide
			Arbitrary<LocalDate> datesAtTheLatest() {
				endDate = Dates.dates().sample();
				return Dates.dates().atTheLatest(endDate);
			}

			@Property
			void between(@ForAll("datesBetween") LocalDate date) {
				assertThat(date).isAfterOrEqualTo(startDate);
				assertThat(date).isBeforeOrEqualTo(endDate);
			}

			@Provide
			Arbitrary<LocalDate> datesBetween() {
				startDate = Dates.dates().sample();
				endDate = Dates.dates().atTheEarliest(startDate).sample();
				return Dates.dates().between(startDate, endDate);
			}

			@Property
			void betweenSame(@ForAll("datesBetweenSame") LocalDate date) {
				assertThat(date).isEqualTo(date);
			}

			@Provide
			Arbitrary<LocalDate> datesBetweenSame() {
				startDate = Dates.dates().sample();
				endDate = startDate;
				return Dates.dates().between(startDate, endDate);
			}

		}

		@Group
		class YearMethods {

			private int startInt;
			private int endInt;

			@Property
			void yearGreaterOrEqual(@ForAll("yearsGreaterOrEqual") LocalDate date) {
				assertThat(date.getYear()).isGreaterThanOrEqualTo(startInt);
			}

			@Provide
			Arbitrary<LocalDate> yearsGreaterOrEqual() {
				startInt = Arbitraries.integers().between(LocalDate.MIN.getYear(), LocalDate.MAX.getYear()).sample();
				return Dates.dates().yearGreaterOrEqual(startInt);
			}

			@Property
			void yearLessOrEqual(@ForAll("yearsLessOrEqual") LocalDate date) {
				assertThat(date.getYear()).isLessThanOrEqualTo(endInt);
			}

			@Provide
			Arbitrary<LocalDate> yearsLessOrEqual() {
				endInt = Arbitraries.integers().between(LocalDate.MIN.getYear(), LocalDate.MAX.getYear()).sample();
				return Dates.dates().yearLessOrEqual(endInt);
			}

			@Property
			void yearBetween(@ForAll("yearsBetween") LocalDate date) {
				assertThat(date.getYear()).isGreaterThanOrEqualTo(startInt);
				assertThat(date.getYear()).isLessThanOrEqualTo(endInt);
			}

			@Provide
			Arbitrary<LocalDate> yearsBetween() {
				startInt = Arbitraries.integers().between(LocalDate.MIN.getYear(), LocalDate.MAX.getYear()).sample();
				endInt = Arbitraries.integers().between(startInt, LocalDate.MAX.getYear()).sample();
				return Dates.dates().yearBetween(startInt, endInt);
			}

			@Property
			void yearBetweenSame(@ForAll("yearsBetweenSame") LocalDate date) {
				assertThat(date.getYear()).isEqualTo(startInt);
			}

			@Provide
			Arbitrary<LocalDate> yearsBetweenSame() {
				startInt = Arbitraries.integers().between(LocalDate.MIN.getYear(), LocalDate.MAX.getYear()).sample();
				endInt = startInt;
				return Dates.dates().yearBetween(startInt, endInt);
			}

		}

		@Group
		class MonthMethods {

			private Month startMonth;
			private Month endMonth;

			private Month intToMonth(int month){
				switch (month){
					case 1:
						return Month.JANUARY;
					case 2:
						return Month.FEBRUARY;
					case 3:
						return Month.MARCH;
					case 4:
						return Month.APRIL;
					case 5:
						return Month.MAY;
					case 6:
						return Month. JUNE;
					case 7:
						return Month.JULY;
					case 8:
						return Month.AUGUST;
					case 9:
						return Month.SEPTEMBER;
					case 10:
						return Month.OCTOBER;
					case 11:
						return Month.NOVEMBER;
					case 12:
						return Month.DECEMBER;
					default:
						return null;
				}
			}

			@Property
			void monthGreaterOrEqual(@ForAll("monthsGreaterOrEqual") LocalDate date) {
				assertThat(date.getMonth()).isGreaterThanOrEqualTo(startMonth);
			}

			@Provide
			Arbitrary<LocalDate> monthsGreaterOrEqual() {
				startMonth = intToMonth(Arbitraries.integers().between(1, 12).sample());
				return Dates.dates().monthGreaterOrEqual(startMonth);
			}

			@Property
			void monthLessOrEqual(@ForAll("monthsLessOrEqual") LocalDate date) {
				assertThat(date.getMonth()).isLessThanOrEqualTo(endMonth);
			}

			@Provide
			Arbitrary<LocalDate> monthsLessOrEqual() {
				endMonth = intToMonth(Arbitraries.integers().between(1, 12).sample());
				return Dates.dates().monthLessOrEqual(endMonth);
			}

			@Property
			void monthBetween(@ForAll("monthsBetween") LocalDate date) {
				assertThat(date.getMonth()).isGreaterThanOrEqualTo(startMonth);
				assertThat(date.getMonth()).isLessThanOrEqualTo(endMonth);
			}

			@Provide
			Arbitrary<LocalDate> monthsBetween() {
				int start = Arbitraries.integers().between(1, 12).sample();
				startMonth = intToMonth(start);
				endMonth = intToMonth(Arbitraries.integers().between(start, 12).sample());
				return Dates.dates().monthBetween(startMonth, endMonth);
			}

			@Property
			void monthBetweenSame(@ForAll("monthsBetweenSame") LocalDate date) {
				assertThat(date.getMonth()).isEqualTo(startMonth);
			}

			@Provide
			Arbitrary<LocalDate> monthsBetweenSame() {
				startMonth = intToMonth(Arbitraries.integers().between(1, 12).sample());
				endMonth = startMonth;
				return Dates.dates().monthBetween(startMonth, endMonth);
			}

		}

		@Group
		class DayOfMonthMethods {

			private int startInt;
			private int endInt;

			@Property
			void dayOfMonthGreaterOrEqual(@ForAll("dayOfMonthsGreaterOrEqual") LocalDate date) {
				assertThat(date.getDayOfMonth()).isGreaterThanOrEqualTo(startInt);
			}

			@Provide
			Arbitrary<LocalDate> dayOfMonthsGreaterOrEqual() {
				startInt = Arbitraries.integers().between(1, 31).sample();
				return Dates.dates().dayOfMonthGreaterOrEqual(startInt);
			}

			@Property
			void dayOfMonthLessOrEqual(@ForAll("dayOfMonthsLessOrEqual") LocalDate date) {
				assertThat(date.getDayOfMonth()).isLessThanOrEqualTo(endInt);
			}

			@Provide
			Arbitrary<LocalDate> dayOfMonthsLessOrEqual() {
				endInt = Arbitraries.integers().between(1, 31).sample();
				return Dates.dates().dayOfMonthLessOrEqual(endInt);
			}

			@Property
			void dayOfMonthBetween(@ForAll("dayOfMonthsBetween") LocalDate date) {
				assertThat(date.getDayOfMonth()).isGreaterThanOrEqualTo(startInt);
				assertThat(date.getDayOfMonth()).isLessThanOrEqualTo(endInt);
			}

			@Provide
			Arbitrary<LocalDate> dayOfMonthsBetween() {
				startInt = Arbitraries.integers().between(1, 31).sample();
				endInt = Arbitraries.integers().between(startInt, 31).sample();
				return Dates.dates().dayOfMonthBetween(startInt, endInt);
			}

			@Property
			void dayOfMonthBetweenSame(@ForAll("dayOfMonthsBetweenSame") LocalDate date) {
				assertThat(date.getDayOfMonth()).isEqualTo(startInt);
			}

			@Provide
			Arbitrary<LocalDate> dayOfMonthsBetweenSame() {
				startInt = Arbitraries.integers().between(1, 31).sample();
				endInt = startInt;
				return Dates.dates().dayOfMonthBetween(startInt, endInt);
			}

		}

		@Group
		class OnlyDaysOfWeekMethods {

			private DayOfWeek[] dayOfWeeks;

			private DayOfWeek[] generateDayOfWeeks(){
				int count = Arbitraries.integers().between(1, 7).sample();
				Arbitrary<DayOfWeek> dayOfWeekArbitrary = Arbitraries.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
				ArrayList<DayOfWeek> dayOfWeekArrayList = new ArrayList<>();
				for(int i = 0; i < count; i++){
					DayOfWeek toAdd = dayOfWeekArbitrary.sample();
					dayOfWeekArbitrary = dayOfWeekArbitrary.filter(v -> !v.equals(toAdd));
					dayOfWeekArrayList.add(toAdd);
				}
				return dayOfWeekArrayList.toArray(new DayOfWeek[]{});
			}

			@Property
			void onlyDaysOfWeek(@ForAll("onlyDayOfWeeks") LocalDate localDate) {
				assertThat(localDate.getDayOfWeek()).isIn(dayOfWeeks);
			}

			@Provide
			Arbitrary<LocalDate> onlyDayOfWeeks() {
				dayOfWeeks = generateDayOfWeeks();
				return Dates.dates().onlyDaysOfWeek(dayOfWeeks);
			}

		}

	}

}
