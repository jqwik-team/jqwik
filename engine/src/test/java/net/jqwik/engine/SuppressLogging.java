package net.jqwik.engine;

import java.lang.annotation.*;
import java.util.*;
import java.util.logging.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.hooks.*;

/**
 * Used to annotate properties that should not log anything
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AddLifecycleHook(SuppressLogging.Hook.class)
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
			return Hooks.AroundProperty.SUPPRESS_LOGGING_PROXIMITY;
		}

	}
}