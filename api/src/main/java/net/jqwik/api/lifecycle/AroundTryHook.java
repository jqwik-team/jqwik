package net.jqwik.api.lifecycle;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Implement this hook to define behaviour that should "wrap" the execution of a single try,
 * i.e., do something directly before or after executing a property method with a given list of parameters.
 * You can even change the result of a try from satisfied to falsified or the other way round.
 */
@API(status = EXPERIMENTAL, since = "1.2.3")
@FunctionalInterface
public interface AroundTryHook extends LifecycleHook {

	/**
	 * When you wrap a try you can do stuff before and/or after its execution.
	 * All implementors should invoke the try with {@code aTry.execute(parameters)}
	 * and either return the result of this call or create another result.
	 *
	 * <p>
	 * It is possible to manipulate the {@code parameters} before invoking
	 * {@code aTry.execute(parameters)}.
	 * Never change the number and types of parameters because this will result
	 * in runtime errors.
	 * Also keep in mind that
	 * all manipulation might mess up random generation and shrinking.
	 * </p>
	 *
	 * @param context    The property's context object
	 * @param aTry       executor to call
	 * @param parameters the generated parameters for this try
	 * @return result of running a single try
	 */
	TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) throws Throwable;

	/**
	 * The higher the value, the closer to the actual property method.
	 * Default value is 0.
	 *
	 * <p>
	 * Values greater than -10 will make it run "inside"
	 * annotated lifecycle methods ({@linkplain BeforeTry} and {@linkplain AfterTry}).
	 * </p>
	 *
	 * @return an integer value
	 */
	default int aroundTryProximity() {
		return 0;
	}

	@API(status = INTERNAL)
	AroundTryHook BASE = (tryLifecycleContext, aTry, parameters) -> aTry.execute(parameters);

	@API(status = INTERNAL)
	default int compareTo(AroundTryHook other) {
		return Integer.compare(this.aroundTryProximity(), other.aroundTryProximity());
	}

}
