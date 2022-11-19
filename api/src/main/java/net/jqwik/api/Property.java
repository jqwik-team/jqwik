package net.jqwik.api;

import java.lang.annotation.*;

import org.apiguardian.api.*;
import org.junit.platform.commons.annotation.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use {@code @Property} to mark methods that serve as properties.
 * Those methods usually have one or more parameters annotated with {@linkplain ForAll}.
 * <p>
 * They are executed (tried) several times,
 * either until they fail or until the configured number of {@code tries()} has been reached.
 * <p>
 * Just like methods annotated with {@linkplain Example} example, annotated methods
 * must not be private. They can either return {@code Boolean}, {@code boolean}
 * or {@code void}.
 * <p>
 * For more info, you can have a look at the user guide,
 * <a href="https://jqwik.net/docs/current/user-guide.html#optional-property-parameters">optional-property-parameters</a>.
 *
 * @see Example
 * @see ShrinkingMode
 * @see GenerationMode
 * @see AfterFailureMode
 * @see EdgeCasesMode
 * @see Data
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Testable
@API(status = STABLE, since = "1.0")
public @interface Property {
	int TRIES_NOT_SET = 0;
	int MAX_DISCARD_RATIO_NOT_SET = 0;
	String SEED_NOT_SET = "";
	String SEED_FROM_NAME = "SEED_FROM_NAME";
	String STEREOTYPE_NOT_SET = "";

	/**
	 * Tries are the test runs with different parameters. By default it is 1000. You can override globally in the property file
	 * (see <a href="https://jqwik.net/docs/current/user-guide.html#jqwik-configuration">jqwik.properties</a>, or here, in {@link Property}
	 * annotation.
	 *
	 * @return number of tries to run
	 */
	int tries() default TRIES_NOT_SET;

	/**
	 * The maximum ratio of tried versus actually checked property runs in case you are using Assumptions. If the ratio is exceeded jqwik
	 * will report this property as a failure.
	 *
	 * <p>
	 * The default is 5 which can be overridden in <a href="https://jqwik.net/docs/current/user-guide.html#jqwik-configuration">jqwik.properties</a>.
	 *
	 * @return the maximum ration
	 *
	 */
	int maxDiscardRatio() default MAX_DISCARD_RATIO_NOT_SET;

	String seed() default SEED_NOT_SET;

	/**
	 * Controls how shrinking is done when falsified property is found.
	 * <p>
	 * Default value is {@link ShrinkingMode#BOUNDED}, i.e. shrinking is tried to a depth of 1000 steps maximum per value.
	 *
	 * @return the shrinking mode
	 */
	ShrinkingMode shrinking() default ShrinkingMode.NOT_SET;

	String stereotype() default STEREOTYPE_NOT_SET;

	@API(status = MAINTAINED, since = "1.0")
	GenerationMode generation() default GenerationMode.NOT_SET;

	@API(status = MAINTAINED, since = "1.0")
	AfterFailureMode afterFailure() default AfterFailureMode.NOT_SET;

	@API(status = EXPERIMENTAL, since = "1.3.0")
	EdgeCasesMode edgeCases() default EdgeCasesMode.NOT_SET;

	/**
	 * Controls how to behave if a {@link #seed()} is present.
	 * <p>
	 * Default value is the value from the global {@code jqwik.seeds.whenfixed}
	 * configuration property.
	 *
	 * @return the fixed seed mode
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	FixedSeedMode whenFixedSeed() default FixedSeedMode.NOT_SET;
}
