package net.jqwik.time.api.dates.calendar;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.time.api.*;
import net.jqwik.time.internal.properties.arbitraries.*;

@PropertyDefaults(edgeCases = EdgeCasesMode.NONE)
@StatisticsReport(onFailureOnly = true)
public class EqualDistributionTests {

	@Provide
	Arbitrary<Calendar> dates() {
		return Dates.datesAsCalendar();
	}

	@Property
	void months(@ForAll("dates") Calendar date) {
		Statistics.label("Months")
				  .collect(date.get(Calendar.MONTH))
				  .coverage(this::checkMonthCoverage);
	}

	@Property
	void dayOfMonths(@ForAll("dates") Calendar date) {
		Statistics.label("Day of months")
				  .collect(date.get(Calendar.DAY_OF_MONTH))
				  .coverage(this::checkDayOfMonthCoverage);
	}

	@Property
	void dayOfWeeks(@ForAll("dates") Calendar date) {
		Statistics.label("Day of weeks")
				  .collect(DefaultCalendarArbitrary.calendarDayOfWeekToDayOfWeek(date))
				  .coverage(this::checkDayOfWeekCoverage);
	}

	@Property
	void leapYears(@ForAll("dates") Calendar date) {
		Statistics.label("Leap years")
				  .collect(new GregorianCalendar().isLeapYear(date.get(Calendar.YEAR)))
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
