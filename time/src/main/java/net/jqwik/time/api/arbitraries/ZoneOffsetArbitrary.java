package net.jqwik.time.api.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of zone offset values.
 */
@API(status = EXPERIMENTAL, since = "1.4.1")
public interface ZoneOffsetArbitrary extends Arbitrary<ZoneOffset> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated zone offset values.
	 */
	default ZoneOffsetArbitrary between(ZoneOffset min, ZoneOffset max) {
		if (min.getTotalSeconds() > max.getTotalSeconds()) {
			return atTheEarliest(max).atTheLatest(min);
		}
		return atTheEarliest(min).atTheLatest(max);
	}

	/**
	 * Set the allowed lower {@code min} (included) bounder of generated zone offset values.
	 */
	ZoneOffsetArbitrary atTheEarliest(ZoneOffset min);

	/**
	 * Set the allowed upper {@code max} (included) bounder of generated zone offset values.
	 */
	ZoneOffsetArbitrary atTheLatest(ZoneOffset max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated hour values.
	 * The hours can be between {@code -18} and {@code 18}.
	 */
	ZoneOffsetArbitrary hourBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated minute values.
	 * The minutes can be between {@code 0} and {@code 59}.
	 */
	ZoneOffsetArbitrary minuteBetween(int min, int max);

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated second values.
	 * The minutes can be between {@code 0} and {@code 59}.
	 */
	ZoneOffsetArbitrary secondBetween(int min, int max);

}
