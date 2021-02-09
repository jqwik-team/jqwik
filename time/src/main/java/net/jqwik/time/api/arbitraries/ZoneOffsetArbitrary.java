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

}
