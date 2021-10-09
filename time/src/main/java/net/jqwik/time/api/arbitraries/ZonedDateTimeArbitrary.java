package net.jqwik.time.api.arbitraries;

import java.time.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of zoned date time values.
 * All generated zoned date times use the Gregorian Calendar, even if they are before October 15, 1582.
 * By default, zoned date times with years between 1900 and 2500 are generated.
 */
@API(status = EXPERIMENTAL, since = "1.6.0")
public interface ZonedDateTimeArbitrary extends Arbitrary<OffsetDateTime> {

}
