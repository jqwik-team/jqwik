package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.lifecycle.TryExecutionResult.Status.*;

public class CheckedPropertyFactoryTests {

	private CheckedPropertyFactory factory = new CheckedPropertyFactory();

	@Example
	void simple() {
		PropertyMethodDescriptor descriptor = createDescriptor("prop", "42", 11, 4, ShrinkingMode.OFF);

		CheckedProperty property = factory.fromDescriptor(
			descriptor,
			createPropertyContext(descriptor),
			AroundTryHook.BASE,
			ResolveParameterHook.DO_NOT_RESOLVE
		);

		assertThat(property.propertyName).isEqualTo("prop");

		assertThat(property.propertyParameters).size().isEqualTo(2);
		assertThat(property.forAllParameters).size().isEqualTo(2);
		assertThat(property.propertyParameters.get(0).getType()).isEqualTo(int.class);
		assertThat(property.propertyParameters.get(1).getType()).isEqualTo(String.class);

		List<Object> argsTrue = Arrays.asList(1, "test");
		List<Object> argsFalse = Arrays.asList(2, "test");
		assertThat(property.tryLifecycleExecutor.execute(null, argsTrue).status()).isEqualTo(SATISFIED);
		assertThat(property.tryLifecycleExecutor.execute(null, argsFalse).status()).isEqualTo(FALSIFIED);

		assertThat(property.configuration.getStereotype()).isEqualTo("Property");
		assertThat(property.configuration.getSeed()).isEqualTo("42");
		assertThat(property.configuration.getTries()).isEqualTo(11);
		assertThat(property.configuration.getMaxDiscardRatio()).isEqualTo(4);
	}

	private PropertyLifecycleContext createPropertyContext(PropertyMethodDescriptor descriptor) {
		return new DefaultPropertyLifecycleContext(descriptor, new PropertyExamples(), ((key, value) -> {}), ResolveParameterHook.DO_NOT_RESOLVE);
	}

	@Example
	void withUnboundParams() {
		PropertyMethodDescriptor descriptor = createDescriptor("propWithUnboundParams", "42", 11, 5, ShrinkingMode.OFF);
		CheckedProperty property = factory.fromDescriptor(
			descriptor,
			createPropertyContext(descriptor),
			AroundTryHook.BASE,
			ResolveParameterHook.DO_NOT_RESOLVE
		);

		assertThat(property.propertyParameters).size().isEqualTo(4);
		assertThat(property.forAllParameters).size().isEqualTo(2);
		assertThat(property.propertyParameters.get(0).getType()).isEqualTo(int.class);
		assertThat(property.propertyParameters.get(0).getAnnotation(ForAll.class)).isNull();
		assertThat(property.propertyParameters.get(1).getType()).isEqualTo(int.class);
		assertThat(property.propertyParameters.get(1).getAnnotation(ForAll.class)).isNotNull();
		assertThat(property.propertyParameters.get(2).getType()).isEqualTo(String.class);
		assertThat(property.propertyParameters.get(2).getAnnotation(ForAll.class)).isNotNull();
		assertThat(property.propertyParameters.get(3).getType()).isEqualTo(String.class);
		assertThat(property.propertyParameters.get(3).getAnnotation(ForAll.class)).isNull();
	}

	@Example
	void withNoParamsAndVoidResult() {
		PropertyMethodDescriptor descriptor = createDescriptor("propWithVoidResult", "42", 11, 5, ShrinkingMode.OFF);
		CheckedProperty property = factory.fromDescriptor(
			descriptor,
			createPropertyContext(descriptor),
			AroundTryHook.BASE,
			ResolveParameterHook.DO_NOT_RESOLVE
		);

		assertThat(property.propertyParameters).size().isEqualTo(0);

		List<Object> noArgs = Arrays.asList();
		assertThat(property.tryLifecycleExecutor.execute(null, noArgs).status()).isEqualTo(SATISFIED);
	}

	private PropertyMethodDescriptor createDescriptor(
		String methodName, String seed, int tries, int maxDiscardRatio,
		ShrinkingMode shrinking
	) {
		Class<PropertyExamples> containerClass = PropertyExamples.class;
		return TestHelper.createPropertyMethodDescriptor(containerClass, methodName, seed, tries, maxDiscardRatio, shrinking);
	}

	private static class PropertyExamples {

		@Property
		boolean prop(@ForAll int anInt, @ForAll String aString) {
			return anInt == 1 && aString.equals("test");
		}

		@Property
		boolean propWithUnboundParams(int otherInt, @ForAll int anInt, @ForAll String aString, String otherString) {
			return true;
		}

		@Property(tries = 42)
		boolean propWithTries(@ForAll int anInt, @ForAll String aString) {
			return true;
		}

		@Property(seed = "4242")
		boolean propWithSeed(@ForAll int anInt, @ForAll String aString) {
			return true;
		}

		@Property
		void propWithVoidResult() {
		}

	}
}
