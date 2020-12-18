package net.jqwik.api.time;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of month and day values.
 */
@API(status = EXPERIMENTAL, since = "1.4.0")
public interface MonthDayArbitrary extends Arbitrary<MonthDay> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month and day values.
	 */
	default MonthDayArbitrary between(MonthDay min, MonthDay max) {
		return atTheEarliest(min).atTheLatest(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated month and day values.
	 */
	MonthDayArbitrary atTheEarliest(MonthDay min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated month and day values.
	 */
	MonthDayArbitrary atTheLatest(MonthDay max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 */
	default MonthDayArbitrary monthBetween(Month min, Month max) {
		return monthGreaterOrEqual(min).monthLessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated month values.
	 */
	default MonthDayArbitrary monthBetween(int min, int max) {
		return monthBetween(Month.of(min), Month.of(max));
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated month values.
	 */
	MonthDayArbitrary monthGreaterOrEqual(Month min);

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated month values.
	 */
	default MonthDayArbitrary monthGreaterOrEqual(int min) {
		return monthGreaterOrEqual(Month.of(min));
	}

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated month values.
	 */
	MonthDayArbitrary monthLessOrEqual(Month max);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated month values.
	 */
	default MonthDayArbitrary monthLessOrEqual(int max) {
		return monthLessOrEqual(Month.of(max));
	}

	/**
	 * Set an array of allowed {@code months}.
	 */
	MonthDayArbitrary onlyMonths(Month... months);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated day of month values.
	 */
	default MonthDayArbitrary dayOfMonthBetween(int min, int max) {
		return dayOfMonthGreaterOrEqual(min).dayOfMonthLessOrEqual(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated day of month values.
	 */
	MonthDayArbitrary dayOfMonthGreaterOrEqual(int min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated day of month values.
	 */
	MonthDayArbitrary dayOfMonthLessOrEqual(int max);

}
