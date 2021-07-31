package net.jqwik.time.api.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of instant values.
 * All generated instants use the Gregorian Calendar, even if they are before October 15, 1582.
 * By default, instants with years between 1900 and 2500 are generated.
 */
@API(status = EXPERIMENTAL, since = "1.5.5")
public interface InstantArbitrary extends Arbitrary<Instant> {

}
