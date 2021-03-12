package net.jqwik.time.api.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of local date time values.
 * All generated date times use the Gregorian Calendar, even if they are before October 15, 1582.
 * By default, local date times with years between 1900 and 2500 are generated.
 */
@API(status = EXPERIMENTAL, since = "1.5.1")
public interface LocalDateTimeArbitrary extends Arbitrary<LocalDateTime> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated local date time values.
	 */
	default LocalDateTimeArbitrary between(LocalDateTime min, LocalDateTime max) {
		if (min.isAfter(max)) {
			return atTheEarliest(max).atTheLatest(min);
		}
		return atTheEarliest(min).atTheLatest(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated local date time values.
	 */
	LocalDateTimeArbitrary atTheEarliest(LocalDateTime min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated local date time values.
	 */
	LocalDateTimeArbitrary atTheLatest(LocalDateTime max);

}
