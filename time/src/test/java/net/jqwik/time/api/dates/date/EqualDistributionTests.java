package net.jqwik.time.api.dates.date;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.time.api.*;
import net.jqwik.time.internal.properties.arbitraries.*;

import static net.jqwik.time.api.testingSupport.ForDate.*;

@PropertyDefaults(tries = 2000, edgeCases = EdgeCasesMode.NONE)
public class EqualDistributionTests {

	@Provide
	Arbitrary<Date> dates() {
		return Dates.datesAsDate();
	}

	@Property
	void months(@ForAll("dates") Date date) {
		Statistics.label("Months")
				  .collect(dateToCalendar(date).get(Calendar.MONTH))
				  .coverage(this::checkMonthCoverage);
	}

	@Property
	void dayOfMonths(@ForAll("dates") Date date) {
		Statistics.label("Day of months")
				  .collect(dateToCalendar(date).get(Calendar.DAY_OF_MONTH))
				  .coverage(this::checkDayOfMonthCoverage);
	}

	@Property
	void dayOfWeeks(@ForAll("dates") Date date) {
		Statistics.label("Day of weeks")
				  .collect(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(dateToCalendar(date)))
				  .coverage(this::checkDayOfWeekCoverage);
	}

	@Property
	void leapYears(@ForAll("dates") Date date) {
		Statistics.label("Leap years")
				  .collect(new GregorianCalendar().isLeapYear(dateToCalendar(date).get(Calendar.YEAR)))
				  .coverage(coverage -> {
					  coverage.check(true).percentage(p -> p >= 20);
					  coverage.check(false).percentage(p -> p >= 65);
				  });
	}

	private void checkMonthCoverage(StatisticsCoverage coverage) {
		Month[] months = Month.class.getEnumConstants();
		for (Month m : months) {
			coverage.check(DefaultCalendarArbitrary.monthToCalendarMonth(m)).percentage(p -> p >= 4);
		}
	}

	private void checkDayOfMonthCoverage(StatisticsCoverage coverage) {
		for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
			coverage.check(dayOfMonth).percentage(p -> p >= 0.5);
		}
	}

	private void checkDayOfWeekCoverage(StatisticsCoverage coverage) {
		DayOfWeek[] dayOfWeeks = DayOfWeek.class.getEnumConstants();
		for (DayOfWeek dayOfWeek : dayOfWeeks) {
			coverage.check(dayOfWeek).percentage(p -> p >= 9);
		}
	}

}
