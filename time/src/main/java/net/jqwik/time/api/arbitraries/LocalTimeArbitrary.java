package net.jqwik.time.api.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of local time values.
 */
@API(status = EXPERIMENTAL, since = "1.4.1")
public interface LocalTimeArbitrary extends Arbitrary<LocalTime> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated local time values.
	 */
	default LocalTimeArbitrary between(LocalTime min, LocalTime max) {
		if (min.isAfter(max)) {
			return atTheEarliest(max).atTheLatest(min);
		}
		return atTheEarliest(min).atTheLatest(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated local time values.
	 */
	LocalTimeArbitrary atTheEarliest(LocalTime min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated local time values.
	 */
	LocalTimeArbitrary atTheLatest(LocalTime max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated hour values.
	 * The hours can be between {@code 0} and {@code 23}.
	 */
	LocalTimeArbitrary hourBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated minute values.
	 * The minutes can be between {@code 0} and {@code 59}.
	 */
	LocalTimeArbitrary minuteBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated second values.
	 * The minutes can be between {@code 0} and {@code 59}.
	 */
	LocalTimeArbitrary secondBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated millisecond values.
	 * The millisecond can be between {@code 0} and {@code 999}.
	 */
	LocalTimeArbitrary millisecondBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated microsecond values.
	 * The microsecond can be between {@code 0} and {@code 999}.
	 */
	LocalTimeArbitrary microsecondBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated nanosecond values.
	 * The nanosecond can be between {@code 0} and {@code 999}.
	 */
	LocalTimeArbitrary nanosecondBetween(int min, int max);

}
