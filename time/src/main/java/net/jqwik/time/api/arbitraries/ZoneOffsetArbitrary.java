package net.jqwik.time.api.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of zone offset values.
 * All generated values are between -12:00:00 and +14:00:00.
 * All generated minute values are 0, 15, 30 or 45 with second 0.
 */
@API(status = EXPERIMENTAL, since = "1.5.1")
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

}
