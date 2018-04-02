package net.jqwik.execution;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import org.junit.platform.engine.*;

import java.lang.reflect.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class CheckedPropertyFactoryTests {

	private CheckedPropertyFactory factory = new CheckedPropertyFactory();

	@Example
	void simple() {
		PropertyMethodDescriptor descriptor = createDescriptor("prop", "42", 11, 4, ShrinkingMode.OFF);
		CheckedProperty property = factory.fromDescriptor(descriptor, new PropertyExamples());

		assertThat(property.propertyName).isEqualTo("prop");

		assertThat(property.forAllParameters).size().isEqualTo(2);
		assertThat(property.forAllParameters.get(0).getType()).isEqualTo(int.class);
		assertThat(property.forAllParameters.get(1).getType()).isEqualTo(String.class);

		List<Object> argsTrue = Arrays.asList(1, "test");
		List<Object> argsFalse = Arrays.asList(2, "test");
		assertThat(property.forAllPredicate.test(argsTrue)).isTrue();
		assertThat(property.forAllPredicate.test(argsFalse)).isFalse();

		assertThat(property.configuration.getStereotype()).isEqualTo("Property");
		assertThat(property.configuration.getSeed()).isEqualTo("42");
		assertThat(property.configuration.getTries()).isEqualTo(11);
		assertThat(property.configuration.getMaxDiscardRatio()).isEqualTo(4);
	}

	@Example
	void withUnboundParams() {
		PropertyMethodDescriptor descriptor = createDescriptor("propWithUnboundParams", "42", 11, 5, ShrinkingMode.OFF);
		CheckedProperty property = factory.fromDescriptor(descriptor, new PropertyExamples());

		assertThat(property.forAllParameters).size().isEqualTo(2);
		assertThat(property.forAllParameters.get(0).getType()).isEqualTo(int.class);
		assertThat(property.forAllParameters.get(0).getAnnotation(ForAll.class)).isNotNull();
		assertThat(property.forAllParameters.get(1).getType()).isEqualTo(String.class);
		assertThat(property.forAllParameters.get(1).getAnnotation(ForAll.class)).isNotNull();
	}

	@Example
	void withNoParamsAndVoidResult() {
		PropertyMethodDescriptor descriptor = createDescriptor("propWithVoidResult", "42", 11, 5, ShrinkingMode.OFF);
		CheckedProperty property = factory.fromDescriptor(descriptor, new PropertyExamples());

		assertThat(property.forAllParameters).size().isEqualTo(0);

		List<Object> noArgs = Arrays.asList();
		assertThat(property.forAllPredicate.test(noArgs)).isTrue();
	}

	private PropertyMethodDescriptor createDescriptor(String methodName, String seed, int tries, int maxDiscardRatio,
			ShrinkingMode shrinking) {
		UniqueId uniqueId = UniqueId.root("test", "i dont care");
		Method method = TestHelper.getMethod(PropertyExamples.class, methodName);
		PropertyConfiguration propertyConfig = new PropertyConfiguration("Property", seed, tries, maxDiscardRatio, shrinking, new Reporting[0], 100);
		return new PropertyMethodDescriptor(uniqueId, method, PropertyExamples.class, propertyConfig);
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
