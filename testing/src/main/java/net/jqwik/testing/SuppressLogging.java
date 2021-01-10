package net.jqwik.testing;

import java.lang.annotation.*;
import java.util.*;
import java.util.logging.*;

import org.apiguardian.api.*;

import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Used to annotate properties that should not log anything
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AddLifecycleHook(SuppressLogging.Hook.class)
@API(status = EXPERIMENTAL, since = "1.4.0")
public @interface SuppressLogging {

	/**
	 * Specify a reason for suppressing logging
	 */
	String value() default "";

	class Hook implements AroundPropertyHook {

		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
			Logger rootLogger = Logger.getLogger("");
			List<Handler> consoleHandlers = Arrays.asList(rootLogger.getHandlers());
			consoleHandlers.forEach(rootLogger::removeHandler);
			try {
				return property.execute();
			} finally {
				consoleHandlers.forEach(rootLogger::addHandler);
			}
		}

		@Override
		public int aroundPropertyProximity() {
			return 50;
		}

		@Override
		public PropagationMode propagateTo() {
			return PropagationMode.ALL_DESCENDANTS;
		}
	}
}