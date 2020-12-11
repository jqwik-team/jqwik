package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Implement this hook to define behaviour that should "wrap" the execution of a property,
 * i.e., do something directly before or after running a property - or both.
 * You can even change the result of a property from successful to failed or the other way round.
 */
@API(status = MAINTAINED, since = "1.4.0")
@FunctionalInterface
public interface AroundPropertyHook extends LifecycleHook {

	/**
	 * When you wrap a property you can do stuff before and/or after its execution.
	 * All implementors should invoke the property with {@code property.execute()}
	 * and either return the result of this call or map it to another result.
	 *
	 * @param context  The property's context object
	 * @param property An executor to run the property
	 * @return The result of running the property
	 */
	PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable;

	/**
	 * The higher the value, the closer to the actual property method.
	 * Default value is 0.
	 *
	 * <p>
	 * Values greater than -10 will make it run "inside"
	 * annotated lifecycle methods ({@linkplain BeforeProperty} and {@linkplain AfterProperty}).
	 * </p>
	 *
	 * @return an integer value
	 */
	default int aroundPropertyProximity() {
		return 0;
	}

	@API(status = INTERNAL)
	AroundPropertyHook BASE = (propertyLifecycleContext, property) -> property.execute();

	@API(status = INTERNAL)
	default int compareTo(AroundPropertyHook other) {
		return Integer.compare(this.aroundPropertyProximity(), other.aroundPropertyProximity());
	}

}
