package net.jqwik.api;

import java.util.function.*;

import org.apiguardian.api.*;
import org.opentest4j.*;

import static org.apiguardian.api.API.Status.*;

@API(status = STABLE, since = "1.0")
public class Assume {

	private Assume() {
	}

	/**
	 * If condition does not hold, the current property method will be aborted,
	 * i.e., it will not be executed but not counted as a try.
	 *
	 * <p>
	 * Assumptions are typically positioned at the beginning of a property method.
	 * </p>
	 *
	 * <p>
	 * A failing assumption in an example test (annotated with {@linkplain Example}
	 * or having a single try) will be reported as a skipped test.
	 * </p>
	 *
	 * @param condition Condition to make the assumption true
	 */
	public static void that(boolean condition) {
		if (!condition) {
			throw new TestAbortedException();
		}
	}

	/**
	 * If condition provided by conditionSupplier does not hold, the current property method will be aborted,
	 * i.e., it will not be executed but not counted as a try.
	 *
	 * <p>
	 * Assumptions are typically positioned at the beginning of a property method.
	 *
	 * @param conditionSupplier supplier for condition to make assumption true
	 */
	public static void that(Supplier<Boolean> conditionSupplier) {
		that(conditionSupplier.get());
	}
}
