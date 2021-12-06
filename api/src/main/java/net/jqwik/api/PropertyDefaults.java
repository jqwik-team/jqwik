package net.jqwik.api;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;

import static net.jqwik.api.Property.*;

/**
 * Annotate a container class with {@code @PropertyDefaults}
 * if you want to set defaults of {@code Property} attributes of all contained property methods.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@AddLifecycleHook(PropertyDefaults.PropertyDefaultsHook.class)
@Inherited
@API(status = MAINTAINED, since = "1.4.0")
public @interface PropertyDefaults {

	int tries() default Property.TRIES_NOT_SET;

	AfterFailureMode afterFailure() default AfterFailureMode.NOT_SET;

	ShrinkingMode shrinking() default ShrinkingMode.NOT_SET;

	GenerationMode generation() default GenerationMode.NOT_SET;

	EdgeCasesMode edgeCases() default EdgeCasesMode.NOT_SET;

	@API(status = MAINTAINED, since = "1.4.0")
	FixedSeedMode whenFixedSeed() default FixedSeedMode.NOT_SET;

	@API(status = MAINTAINED, since = "1.6.2")
	int maxDiscardRatio() default MAX_DISCARD_RATIO_NOT_SET;

	class PropertyDefaultsHook implements AroundPropertyHook {

		@Override
		public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
			List<PropertyDefaults> propertyDefaults = context.findAnnotationsInContainer(PropertyDefaults.class);

			findTries(propertyDefaults).ifPresent(tries -> {
				PropertyAttributes attributes = context.attributes();
				if (!attributes.tries().isPresent()) {
					attributes.setTries(tries);
				}
			});
			findAfterFailure(propertyDefaults).ifPresent(afterFailure -> {
				PropertyAttributes attributes = context.attributes();
				if (!attributes.afterFailure().isPresent()) {
					attributes.setAfterFailure(afterFailure);
				}
			});
			findShrinking(propertyDefaults).ifPresent(shrinking -> {
				PropertyAttributes attributes = context.attributes();
				if (!attributes.shrinking().isPresent()) {
					attributes.setShrinking(shrinking);
				}
			});
			findGeneration(propertyDefaults).ifPresent(generation -> {
				PropertyAttributes attributes = context.attributes();
				if (!attributes.generation().isPresent()) {
					attributes.setGeneration(generation);
				}
			});
			findEdgeCases(propertyDefaults).ifPresent(edgeCases -> {
				PropertyAttributes attributes = context.attributes();
				if (!attributes.edgeCases().isPresent()) {
					attributes.setEdgeCases(edgeCases);
				}
			});
			findFixedSeedMode(propertyDefaults).ifPresent(mode -> {
				PropertyAttributes attributes = context.attributes();
				if (!attributes.whenFixedSeed().isPresent()) {
					attributes.setWhenFixedSeed(mode);
				}
			});
			findMaxDiscardRation(propertyDefaults).ifPresent(ratio -> {
				PropertyAttributes attributes = context.attributes();
				if (!attributes.maxDiscardRatio().isPresent()) {
					attributes.setMaxDiscardRatio(ratio);
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

		private Optional<AfterFailureMode> findAfterFailure(List<PropertyDefaults> propertyDefaults) {
			return propertyDefaults.stream()
								   .map(PropertyDefaults::afterFailure)
								   .filter(afterFailure -> afterFailure != AfterFailureMode.NOT_SET)
								   .findFirst();
		}

		private Optional<ShrinkingMode> findShrinking(List<PropertyDefaults> propertyDefaults) {
			return propertyDefaults.stream()
								   .map(PropertyDefaults::shrinking)
								   .filter(shrinking -> shrinking != ShrinkingMode.NOT_SET)
								   .findFirst();
		}

		private Optional<GenerationMode> findGeneration(List<PropertyDefaults> propertyDefaults) {
			return propertyDefaults.stream()
								   .map(PropertyDefaults::generation)
								   .filter(generation -> generation != GenerationMode.NOT_SET)
								   .findFirst();
		}

		private Optional<EdgeCasesMode> findEdgeCases(List<PropertyDefaults> propertyDefaults) {
			return propertyDefaults.stream()
								   .map(PropertyDefaults::edgeCases)
								   .filter(edgeCases -> edgeCases != EdgeCasesMode.NOT_SET)
								   .findFirst();
		}

		private Optional<FixedSeedMode> findFixedSeedMode(List<PropertyDefaults> propertyDefaults) {
			return propertyDefaults.stream()
								   .map(PropertyDefaults::whenFixedSeed)
								   .filter(mode -> mode != FixedSeedMode.NOT_SET)
								   .findFirst();
		}

		private Optional<Integer> findMaxDiscardRation(List<PropertyDefaults> propertyDefaults) {
			return propertyDefaults.stream()
								   .map(PropertyDefaults::maxDiscardRatio)
								   .filter(ratio -> ratio != MAX_DISCARD_RATIO_NOT_SET)
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
