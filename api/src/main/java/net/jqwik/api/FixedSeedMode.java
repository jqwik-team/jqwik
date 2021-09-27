package net.jqwik.api;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * The fixed seed mode determines how jqwik should behave if a property has an
 * explicit {@link Property#seed()} specified. It can be set in
 * {@linkplain Property#whenFixedSeed()} for any property method. It can be set
 * globally using the property {@code jqwik.seeds.whenfixed}.
 * <p>
 * This may be useful, for instance, to help prevent a build server from using
 * a fixed seed that may have been accidentally committed.
 * <p>
 * If it is not set explicitly mode {@linkplain #ALLOW} will be used.
 *
 * @see Property
 */
@API(status = MAINTAINED, since = "1.4.0")
public enum FixedSeedMode {
	ALLOW,
	WARN,
	FAIL,

	@API(status = INTERNAL)
	NOT_SET;
}
