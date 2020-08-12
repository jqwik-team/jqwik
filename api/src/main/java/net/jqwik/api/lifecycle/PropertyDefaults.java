package net.jqwik.api.lifecycle;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Annotate a container class with {@code @PropertyDefaults}
 * if you want to set defaults of {@code Property} attributes of all contained property methods.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@AddLifecycleHook(PropertyDefaults.PropertyDefaultsHook.class)
@API(status = EXPERIMENTAL, since = "1.3.4")
public @interface PropertyDefaults {

	int tries() default Property.TRIES_NOT_SET;

	class PropertyDefaultsHook implements AroundPropertyHook {

		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
			List<PropertyDefaults> propertyDefaults = context.findAnnotationsInContainer(PropertyDefaults.class);

			Optional<Integer> optionalTries = findTries(propertyDefaults);

			optionalTries.ifPresent(tries -> {
				PropertyAttributes attributes = context.attributes();
				if (!attributes.tries().isPresent()) {
					attributes.setTries(tries);
				}
			});
			return property.execute();
		}

		private Optional<Integer> findTries(List<PropertyDefaults> propertyDefaults) {
			return propertyDefaults.stream()
								   .map(PropertyDefaults::tries)
								   .filter(tries -> tries != Property.TRIES_NOT_SET)
								   .findFirst();
		}

		@Override
		public int aroundPropertyProximity() {
			// Somewhat more distant than standard hooks, so that those may change the attributes
			return -10;
		}

		@Override
		public PropagationMode propagateTo() {
			return PropagationMode.ALL_DESCENDANTS;
		}

		@Override
		public boolean appliesTo(Optional<AnnotatedElement> element) {
			return element.map(e -> e instanceof Method).orElse(false);
		}
	}
}
