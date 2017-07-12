package net.jqwik.discovery;

import java.lang.reflect.*;
import java.util.*;

import org.assertj.core.api.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.TestExecutionResult.*;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.recording.*;

@Group
class PropertyMethodResolverTest {

	private TestRunData testRunData = new TestRunData();
	private PropertyMethodResolver resolver = new PropertyMethodResolver(testRunData);

	@Group
	class ResolveElement {
		@Example
		void plainProperty() throws NoSuchMethodException {
			ContainerClassDescriptor classDescriptor = (ContainerClassDescriptor) TestDescriptorBuilder.forClass(TestContainer.class)
					.build();
			Method method = TestHelper.getMethod(TestContainer.class, "plainProperty");
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();
			Assertions.assertThat(propertyMethodDescriptor.getSeed()).isEqualTo(Property.DEFAULT_SEED);
			Assertions.assertThat(propertyMethodDescriptor.getTries()).isEqualTo(Property.DEFAULT_TRIES);
			Assertions.assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("plainProperty");
			Assertions.assertThat(propertyMethodDescriptor.getUniqueId())
					.isEqualTo(classDescriptor.getUniqueId().append("property", method.getName() + "()"));
		}

		@Example
		void propertyWithParams() throws NoSuchMethodException {
			ContainerClassDescriptor classDescriptor = (ContainerClassDescriptor) TestDescriptorBuilder.forClass(TestContainer.class)
					.build();
			Method method = TestHelper.getMethod(TestContainer.class, "propertyWithParams");
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();
			Assertions.assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("propertyWithParams");
			Assertions.assertThat(propertyMethodDescriptor.getUniqueId())
					.isEqualTo(classDescriptor.getUniqueId().append("property","propertyWithParams(int, java.lang.String)"));
		}

		@Example
		void propertyWithAnnotationParams() throws NoSuchMethodException {
			ContainerClassDescriptor classDescriptor = (ContainerClassDescriptor) TestDescriptorBuilder.forClass(TestContainer.class)
					.build();
			Method method = TestHelper.getMethod(TestContainer.class, "withPropertyParams");
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();
			Assertions.assertThat(propertyMethodDescriptor.getSeed()).isEqualTo(42);
			Assertions.assertThat(propertyMethodDescriptor.getTries()).isEqualTo(99);
		}

		@Example
		void propertyThatPreviouslyFailed() throws NoSuchMethodException {
			ContainerClassDescriptor classDescriptor = (ContainerClassDescriptor) TestDescriptorBuilder.forClass(TestContainer.class)
					.build();
			Method method = TestHelper.getMethod(TestContainer.class, "previouslyFailed");
			UniqueId previouslyFailedId = JqwikUniqueIDs.appendProperty(classDescriptor.getUniqueId(), method);
			testRunData.add(new TestRun(previouslyFailedId, Status.FAILED, 4243L));
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();
			Assertions.assertThat(propertyMethodDescriptor.getSeed()).isEqualTo(4243L);
		}

		@Example
		void explicitSeedOverwritesSeedFromPreviouslyFailedTestRun() throws NoSuchMethodException {
			ContainerClassDescriptor classDescriptor = (ContainerClassDescriptor) TestDescriptorBuilder.forClass(TestContainer.class)
					.build();
			Method method = TestHelper.getMethod(TestContainer.class, "withSeed41");
			UniqueId previouslyFailedId = JqwikUniqueIDs.appendProperty(classDescriptor.getUniqueId(), method);
			testRunData.add(new TestRun(previouslyFailedId, Status.FAILED, 9999L));
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();
			Assertions.assertThat(propertyMethodDescriptor.getSeed()).isEqualTo(41L);
		}

	}

	@Group
	class ResolveUniqueId {
		@Example
		void plainProperty() throws NoSuchMethodException {
			Method method = TestHelper.getMethod(TestContainer.class, "plainProperty");
			UniqueId uniqueId = JqwikUniqueIDs.appendProperty(UniqueId.forEngine(JqwikTestEngine.ENGINE_ID), method);
			ContainerClassDescriptor classDescriptor = (ContainerClassDescriptor) TestDescriptorBuilder.forClass(TestContainer.class)
					.build();

			Optional<TestDescriptor> descriptor = resolver.resolveUniqueId(uniqueId.getSegments().get(1), classDescriptor);

			Assertions.assertThat(descriptor).isPresent();
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptor.get();
			Assertions.assertThat(propertyMethodDescriptor.getSeed()).isEqualTo(Property.DEFAULT_SEED);
			Assertions.assertThat(propertyMethodDescriptor.getTries()).isEqualTo(Property.DEFAULT_TRIES);
			Assertions.assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("plainProperty");
		}

		@Example
		void propertyWithParams() throws NoSuchMethodException {
			Method method = TestHelper.getMethod(TestContainer.class, "propertyWithParams");
			UniqueId uniqueId = JqwikUniqueIDs.appendProperty(UniqueId.forEngine(JqwikTestEngine.ENGINE_ID), method);
			ContainerClassDescriptor classDescriptor = (ContainerClassDescriptor) TestDescriptorBuilder.forClass(TestContainer.class)
					.build();

			Optional<TestDescriptor> descriptor = resolver.resolveUniqueId(uniqueId.getSegments().get(1), classDescriptor);

			Assertions.assertThat(descriptor).isPresent();
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptor.get();
			Assertions.assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("propertyWithParams");
		}

	}

	private static class TestContainer {
		@Property
		void plainProperty() {
		}

		@Property
		void propertyWithParams(int anInt, String aString) {
		}

		@Property
		void previouslyFailed() {
		}

		@Property(seed = 41L)
		void withSeed41() {
		}

		@Property(seed = 42L, tries = 99)
		void withPropertyParams() {
		}
	}
}
