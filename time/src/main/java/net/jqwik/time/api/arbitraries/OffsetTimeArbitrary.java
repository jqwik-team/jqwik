package net.jqwik.time.api.arbitraries;

import java.time.*;
import java.time.temporal.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of offset time values.
 */
@API(status = EXPERIMENTAL, since = "1.4.1")
public interface OffsetTimeArbitrary extends Arbitrary<OffsetTime> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated time values.
	 */
	default OffsetTimeArbitrary between(LocalTime min, LocalTime max) {
		if (min.isAfter(max)) {
			return atTheEarliest(max).atTheLatest(min);
		}
		return atTheEarliest(min).atTheLatest(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated time values.
	 */
	OffsetTimeArbitrary atTheEarliest(LocalTime min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated time values.
	 */
	OffsetTimeArbitrary atTheLatest(LocalTime max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated hour values.
	 * The hours can be between {@code 0} and {@code 23}.
	 */
	OffsetTimeArbitrary hourBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated minute values.
	 * The minutes can be between {@code 0} and {@code 59}.
	 */
	OffsetTimeArbitrary minuteBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated second values.
	 * The minutes can be between {@code 0} and {@code 59}.
	 */
	OffsetTimeArbitrary secondBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated offset values.
	 * The offsets can be between {@code -12:00:00} and {@code +14:00:00}.
	 */
	OffsetTimeArbitrary offsetBetween(ZoneOffset min, ZoneOffset max);

	/**
	 * Constrain the precision of generated values.
	 */
	OffsetTimeArbitrary constrainPrecision(ChronoUnit ofPrecision);

}
