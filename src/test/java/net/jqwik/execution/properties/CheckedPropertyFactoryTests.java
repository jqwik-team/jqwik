package net.jqwik.execution.properties;

import javaslang.test.*;
import net.jqwik.api.*;
import net.jqwik.api.properties.*;
import net.jqwik.api.properties.Property;
import net.jqwik.descriptor.*;
import org.junit.platform.engine.*;

import java.lang.reflect.*;

import static org.assertj.core.api.Assertions.*;

public class CheckedPropertyFactoryTests {

	private CheckedPropertyFactory factory = new CheckedPropertyFactory();

	@Example
	void simple() {
		PropertyMethodDescriptor descriptor = createDescriptor("prop");
		CheckedProperty property = factory.fromDescriptor(descriptor, new PropertyExamples());

		assertThat(property.propertyName).isEqualTo("prop");

		assertThat(property.forAllParameters).size().isEqualTo(2);
		assertThat(property.forAllParameters.get(0).getType()).isEqualTo(int.class);
		assertThat(property.forAllParameters.get(1).getType()).isEqualTo(String.class);

		Object[] args = {1, "test"};
		assertThat(property.assumeFunction.apply(args)).isTrue();
		assertThat(property.forAllFunction.apply(args)).isTrue();
		assertThat(property.forAllFunction.apply(new Object[]{2, "test"})).isFalse();

		assertThat(property.randomSeed).isEqualTo(Long.MIN_VALUE);
		assertThat(property.tries).isEqualTo(Checkable.DEFAULT_TRIES);
	}

	@Example
	void withUnboundParams() {
		PropertyMethodDescriptor descriptor = createDescriptor("propWithUnboundParams");
		CheckedProperty property = factory.fromDescriptor(descriptor, new PropertyExamples());

		assertThat(property.forAllParameters).size().isEqualTo(2);
		assertThat(property.forAllParameters.get(0).getType()).isEqualTo(int.class);
		assertThat(property.forAllParameters.get(0).getDeclaredAnnotation(ForAll.class)).isNotNull();
		assertThat(property.forAllParameters.get(1).getType()).isEqualTo(String.class);
		assertThat(property.forAllParameters.get(1).getDeclaredAnnotation(ForAll.class)).isNotNull();
	}

	@Example
	void withTries() {
		PropertyMethodDescriptor descriptor = createDescriptor("propWithTries");
		CheckedProperty property = factory.fromDescriptor(descriptor, new PropertyExamples());
		assertThat(property.tries).isEqualTo(42);
	}

	@Example
	void withSeed() {
		PropertyMethodDescriptor descriptor = createDescriptor("propWithSeed");
		CheckedProperty property = factory.fromDescriptor(descriptor, new PropertyExamples());
		assertThat(property.randomSeed).isEqualTo(4242);
	}

	private PropertyMethodDescriptor createDescriptor(String methodName) {
		UniqueId uniqueId = UniqueId.root("test", "test");
		Method method = ParameterHelper.getMethod(PropertyExamples.class, methodName);
		return new PropertyMethodDescriptor(uniqueId, method, PropertyExamples.class);
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

		@Property(seed = 4242L)
		boolean propWithSeed(@ForAll int anInt, @ForAll String aString) {
			return true;
		}
	}
}
