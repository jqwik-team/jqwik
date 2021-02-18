package net.jqwik.time.api.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of duration values.
 */
@API(status = EXPERIMENTAL, since = "1.5.1")
public interface DurationArbitrary extends Arbitrary<Duration> {

	/**
	 * Set the allowed lower {@code min} (included) and upper {@code max} (included) bounder of generated duration.
	 */
	DurationArbitrary between(Duration min, Duration max);

}
