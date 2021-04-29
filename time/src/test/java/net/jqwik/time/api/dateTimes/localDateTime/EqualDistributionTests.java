package net.jqwik.time.api.dateTimes.localDateTime;

import java.time.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.statistics.*;
import net.jqwik.time.api.*;

import static java.time.temporal.ChronoUnit.*;

@PropertyDefaults(tries = 2000)
public class EqualDistributionTests {

	@Provide
	Arbitrary<LocalDateTime> dateTimes() {
		return DateTimes.dateTimes();
	}

	@Provide
	Arbitrary<LocalDateTime> precisionMilliseconds() {
		return DateTimes.dateTimes().ofPrecision(MILLIS);
	}

	@Provide
	Arbitrary<LocalDateTime> precisionMicroseconds() {
		return DateTimes.dateTimes().ofPrecision(MICROS);
	}

	@Provide
	Arbitrary<LocalDateTime> precisionNanoseconds() {
		return DateTimes.dateTimes().ofPrecision(NANOS);
	}

	@Property
	void months(@ForAll("dateTimes") LocalDateTime dateTime) {
		Statistics.label("Months")
				  .collect(dateTime.getMonth())
				  .coverage(this::checkMonthCoverage);
	}

	@Property
	void dayOfMonths(@ForAll("dateTimes") LocalDateTime dateTime) {
		Statistics.label("Day of months")
				  .collect(dateTime.getDayOfMonth())
				  .coverage(this::checkDayOfMonthCoverage);
	}

	@Property
	void dayOfWeeks(@ForAll("dateTimes") LocalDateTime dateTime) {
		Statistics.label("Day of weeks")
				  .collect(dateTime.getDayOfWeek())
				  .coverage(this::checkDayOfWeekCoverage);
	}

	@Property
	void leapYears(@ForAll("dateTimes") LocalDateTime dateTime) {
		Statistics.label("Leap years")
				  .collect(new GregorianCalendar().isLeapYear(dateTime.getYear()))
				  .coverage(coverage -> {
					  coverage.check(true).percentage(p -> p >= 20);
					  coverage.check(false).percentage(p -> p >= 65);
				  });
	}

	@Property
	void hours(@ForAll("dateTimes") LocalDateTime dateTime) {
		Statistics.label("Hours")
				  .collect(dateTime.getHour())
				  .coverage(this::check24Coverage);
	}

	@Property
	void minutes(@ForAll("dateTimes") LocalDateTime dateTime) {
		Statistics.label("Minutes")
				  .collect(dateTime.getMinute())
				  .coverage(this::check60Coverage);
	}

	@Property
	void seconds(@ForAll("dateTimes") LocalDateTime dateTime) {
		Statistics.label("Seconds")
				  .collect(dateTime.getSecond())
				  .coverage(this::check60Coverage);
	}

	@Property
	void milliseconds(@ForAll("precisionMilliseconds") LocalDateTime dateTime) {

		Statistics.label("Milliseconds x--")
				  .collect(dateTime.getNano() / 100_000_000)
				  .coverage(this::check10Coverage);

		Statistics.label("Milliseconds -x-")
				  .collect((dateTime.getNano() / 10_000_000) % 10)
				  .coverage(this::check10Coverage);

		Statistics.label("Milliseconds --x")
				  .collect((dateTime.getNano() / 1_000_000) % 10)
				  .coverage(this::check10Coverage);

	}

	@Property
	void microseconds(@ForAll("precisionMicroseconds") LocalDateTime dateTime) {

		Statistics.label("Microseconds x--")
				  .collect((dateTime.getNano() % 1_000_000) / 100_000)
				  .coverage(this::check10Coverage);

		Statistics.label("Microseconds -x-")
				  .collect(((dateTime.getNano() % 1_000_000) / 10_000) % 10)
				  .coverage(this::check10Coverage);

		Statistics.label("Microseconds --x")
				  .collect(((dateTime.getNano() % 1_000_000) / 1_000) % 10)
				  .coverage(this::check10Coverage);

	}

	@Property
	void nanoseconds(@ForAll("precisionNanoseconds") LocalDateTime dateTime) {

		Statistics.label("Nanoseconds x--")
				  .collect((dateTime.getNano() % 1_000) / 100)
				  .coverage(this::check10Coverage);

		Statistics.label("Nanoseconds -x-")
				  .collect(((dateTime.getNano() % 1_000) / 10) % 10)
				  .coverage(this::check10Coverage);

		Statistics.label("Nanoseconds --x")
				  .collect((dateTime.getNano() % 1_000) % 10)
				  .coverage(this::check10Coverage);

	}

	private void checkMonthCoverage(StatisticsCoverage coverage) {
		Month[] months = Month.class.getEnumConstants();
		for (Month m : months) {
			coverage.check(m).percentage(p -> p >= 4);
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

	private void check10Coverage(StatisticsCoverage coverage) {
		for (int value = 0; value < 10; value++) {
			coverage.check(value).percentage(p -> p >= 5);
		}
	}

	private void check24Coverage(StatisticsCoverage coverage) {
		for (int value = 0; value < 24; value++) {
			coverage.check(value).percentage(p -> p >= 1.5);
		}
	}

	private void check60Coverage(StatisticsCoverage coverage) {
		for (int value = 0; value < 60; value++) {
			coverage.check(value).percentage(p -> p >= 0.3);
		}
	}

}
