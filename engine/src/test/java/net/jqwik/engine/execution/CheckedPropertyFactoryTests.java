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

		CheckedProperty property = factory.fromDescriptor(descriptor, createPropertyContext(descriptor), AroundTryHook.BASE);

		assertThat(property.propertyName).isEqualTo("prop");

		assertThat(property.forAllParameters).size().isEqualTo(2);
		assertThat(property.forAllParameters.get(0).getType()).isEqualTo(int.class);
		assertThat(property.forAllParameters.get(1).getType()).isEqualTo(String.class);

		List<Object> argsTrue = Arrays.asList(1, "test");
		List<Object> argsFalse = Arrays.asList(2, "test");
		assertThat(property.tryExecutor.execute(argsTrue).status()).isEqualTo(SATISFIED);
		assertThat(property.tryExecutor.execute(argsFalse).status()).isEqualTo(FALSIFIED);

		assertThat(property.configuration.getStereotype()).isEqualTo("Property");
		assertThat(property.configuration.getSeed()).isEqualTo("42");
		assertThat(property.configuration.getTries()).isEqualTo(11);
		assertThat(property.configuration.getMaxDiscardRatio()).isEqualTo(4);
	}

	private PropertyLifecycleContext createPropertyContext(PropertyMethodDescriptor descriptor) {
		return new PropertyLifecycleContextForMethod(descriptor, new PropertyExamples(), ((key, value) -> {}));
	}

	@Example
	void withUnboundParams() {
		PropertyMethodDescriptor descriptor = createDescriptor("propWithUnboundParams", "42", 11, 5, ShrinkingMode.OFF);
		CheckedProperty property = factory.fromDescriptor(descriptor, createPropertyContext(descriptor), AroundTryHook.BASE);

		assertThat(property.forAllParameters).size().isEqualTo(2);
		assertThat(property.forAllParameters.get(0).getType()).isEqualTo(int.class);
		assertThat(property.forAllParameters.get(0).getAnnotation(ForAll.class)).isNotNull();
		assertThat(property.forAllParameters.get(1).getType()).isEqualTo(String.class);
		assertThat(property.forAllParameters.get(1).getAnnotation(ForAll.class)).isNotNull();
	}

	@Example
	void withNoParamsAndVoidResult() {
		PropertyMethodDescriptor descriptor = createDescriptor("propWithVoidResult", "42", 11, 5, ShrinkingMode.OFF);
		CheckedProperty property = factory.fromDescriptor(descriptor, createPropertyContext(descriptor), AroundTryHook.BASE);

		assertThat(property.forAllParameters).size().isEqualTo(0);

		List<Object> noArgs = Arrays.asList();
		assertThat(property.tryExecutor.execute(noArgs).status()).isEqualTo(SATISFIED);
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
