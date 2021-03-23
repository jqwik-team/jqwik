package net.jqwik.time.api.arbitraries;

import java.time.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of local time values.
 */
@API(status = EXPERIMENTAL, since = "1.5.1")
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
	 * Constrain the precision of generated values.
	 * Default value: Seconds. If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
	 */
	LocalTimeArbitrary ofPrecision(ChronoUnit ofPrecision);

}
