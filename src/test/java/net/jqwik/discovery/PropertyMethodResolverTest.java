package net.jqwik.discovery;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.recording.*;
import org.assertj.core.api.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.TestExecutionResult.*;

import java.lang.reflect.*;
import java.util.*;

@Group
class PropertyMethodResolverTest {

	private final int DEFAULT_TRIES = 999;
	private final int DEFAULT_MAX_DISCARD_RATIO = 4;
	private final int DEFAULT_MAX_SHRINKING_RATE = 99;

	private TestRunData testRunData = new TestRunData();
	private PropertyDefaultValues propertyDefaultValues = PropertyDefaultValues.with(DEFAULT_TRIES, DEFAULT_MAX_DISCARD_RATIO);
	private PropertyMethodResolver resolver = new PropertyMethodResolver(testRunData, propertyDefaultValues);

	@Group
	class ResolveElement {
		@Example
		void plainProperty() {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);
			Method method = TestHelper.getMethod(TestContainer.class, "plainProperty");
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();


			Assertions.assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("plainProperty");
			Assertions.assertThat(propertyMethodDescriptor.getUniqueId())
					.isEqualTo(classDescriptor.getUniqueId().append("property", method.getName() + "()"));

			assertDefaultConfigurationProperties(propertyMethodDescriptor);
		}

		@Example
		void propertyWithLabel() {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);
			Method method = TestHelper.getMethod(TestContainer.class, "propertyWithLabel");
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();

			Assertions.assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("my label");
			Assertions.assertThat(propertyMethodDescriptor.getUniqueId())
					.isEqualTo(classDescriptor.getUniqueId().append("property", method.getName() + "()"));

		}

		@Example
		void propertyWithTag() {
			PropertyMethodDescriptor propertyMethodDescriptor = resolveMethodInClass("propertyWithOneTag", TestContainer.class);

			Assertions.assertThat(propertyMethodDescriptor.getTags()).containsExactly(TestTag.create("tag1"));
		}

		@Example
		void propertyWithSeveralTags() {
			PropertyMethodDescriptor propertyMethodDescriptor = resolveMethodInClass("propertyWithTwoTags", TestContainer.class);

			Assertions.assertThat(propertyMethodDescriptor.getTags())
					  .containsExactly(TestTag.create("tag1"), TestTag.create("tag2"));
		}

		@Example
		void propertyWithTaggedContainer() {
			PropertyMethodDescriptor propertyMethodDescriptor = resolveMethodInClass("propertyWithTag", TaggedTestContainer.class);

			Assertions.assertThat(propertyMethodDescriptor.getTags())
					  .containsExactlyInAnyOrder(TestTag.create("container-tag"), TestTag.create("property-tag"));
		}

		@Example
		void propertyWithParams() {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);
			Method method = TestHelper.getMethod(TestContainer.class, "propertyWithParams");
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();
			Assertions.assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("propertyWithParams");
			Assertions.assertThat(propertyMethodDescriptor.getUniqueId())
					.isEqualTo(classDescriptor.getUniqueId().append("property", "propertyWithParams(int, java.lang.String)"));
		}

		@Example
		void propertyWithAnnotationParams() {
			PropertyMethodDescriptor propertyMethodDescriptor = resolveMethodInClass("withPropertyParams", TestContainer.class);

			Assertions.assertThat(propertyMethodDescriptor.getConfiguration().getSeed()).isEqualTo("42");
			Assertions.assertThat(propertyMethodDescriptor.getConfiguration().getTries()).isEqualTo(99);
			Assertions.assertThat(propertyMethodDescriptor.getConfiguration().getMaxDiscardRatio()).isEqualTo(6);
			Assertions.assertThat(propertyMethodDescriptor.getConfiguration().getShrinkingMode()).isEqualTo(ShrinkingMode.OFF);
			Assertions.assertThat(propertyMethodDescriptor.getConfiguration().getReporting()).containsExactly(Reporting.GENERATED, Reporting.FALSIFIED);
		}

		@Example
		void propertyThatPreviouslyFailed() {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);
			Method method = TestHelper.getMethod(TestContainer.class, "previouslyFailed");
			UniqueId previouslyFailedId = JqwikUniqueIDs.appendProperty(classDescriptor.getUniqueId(), method);
			testRunData.add(new TestRun(previouslyFailedId, Status.FAILED, "4243"));
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();
			Assertions.assertThat(propertyMethodDescriptor.getConfiguration().getSeed()).isEqualTo("4243");
		}

		@Example
		void explicitSeedOverwritesSeedFromPreviouslyFailedTestRun() {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);
			Method method = TestHelper.getMethod(TestContainer.class, "withSeed41");
			UniqueId previouslyFailedId = JqwikUniqueIDs.appendProperty(classDescriptor.getUniqueId(), method);
			testRunData.add(new TestRun(previouslyFailedId, Status.FAILED, "9999"));
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();
			Assertions.assertThat(propertyMethodDescriptor.getConfiguration().getSeed()).isEqualTo("41");
		}

		private PropertyMethodDescriptor resolveMethodInClass(String methodName, Class<?> containerClass) {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(containerClass);
			Method method = TestHelper.getMethod(containerClass, methodName);
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			Assertions.assertThat(descriptors).hasSize(1);
			PropertyMethodDescriptor propertyMethodDescriptor =
				(PropertyMethodDescriptor) descriptors.iterator().next();
			classDescriptor.addChild(propertyMethodDescriptor);
			return propertyMethodDescriptor;
		}


	}

	@Group
	class ResolveUniqueId {
		@Example
		void plainProperty() {
			Method method = TestHelper.getMethod(TestContainer.class, "plainProperty");
			UniqueId uniqueId = JqwikUniqueIDs.appendProperty(UniqueId.forEngine(JqwikTestEngine.ENGINE_ID), method);
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);

			Optional<TestDescriptor> descriptor = resolver.resolveUniqueId(uniqueId.getSegments().get(1), classDescriptor);

			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptor.get();
			Assertions.assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("plainProperty");
			assertDefaultConfigurationProperties(propertyMethodDescriptor);
		}

		@Example
		void propertyWithParams() {
			Method method = TestHelper.getMethod(TestContainer.class, "propertyWithParams");
			UniqueId uniqueId = JqwikUniqueIDs.appendProperty(UniqueId.forEngine(JqwikTestEngine.ENGINE_ID), method);
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);

			Optional<TestDescriptor> descriptor = resolver.resolveUniqueId(uniqueId.getSegments().get(1), classDescriptor);

			Assertions.assertThat(descriptor).isPresent();
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptor.get();
			Assertions.assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("propertyWithParams");
		}

	}

	private ContainerClassDescriptor buildContainerDescriptor(Class<?> containerClass) {
		return (ContainerClassDescriptor) TestDescriptorBuilder.forClass(containerClass).build();
	}

	private void assertDefaultConfigurationProperties(PropertyMethodDescriptor propertyMethodDescriptor) {
		Assertions.assertThat(propertyMethodDescriptor.getConfiguration().getSeed()).isEqualTo(Property.SEED_NOT_SET);
		Assertions.assertThat(propertyMethodDescriptor.getConfiguration().getTries()).isEqualTo(DEFAULT_TRIES);
		Assertions.assertThat(propertyMethodDescriptor.getConfiguration().getMaxDiscardRatio()).isEqualTo(DEFAULT_MAX_DISCARD_RATIO);
		Assertions.assertThat(propertyMethodDescriptor.getConfiguration().getShrinkingMode()).isEqualTo(ShrinkingMode.FULL);
		Assertions.assertThat(propertyMethodDescriptor.getConfiguration().getReporting()).isEmpty();
	}

	private static class TestContainer {
		@Property
		void plainProperty() {
		}

		@Property
		@Label("my label")
		void propertyWithLabel() {
		}

		@Property
		@Tag("tag1")
		void propertyWithOneTag() {
		}

		@Property
		@Tag("tag1")
		@Tag("tag2")
		void propertyWithTwoTags() {
		}

		@Property
		void propertyWithParams(int anInt, String aString) {
		}

		@Property
		void previouslyFailed() {
		}

		@Property(seed = "41")
		void withSeed41() {
		}

		@Property( //
			seed = "42", //
			tries = 99, //
			maxDiscardRatio = 6, //
			shrinking = ShrinkingMode.OFF, //
			reporting = {Reporting.GENERATED, Reporting.FALSIFIED} //
		)
		void withPropertyParams() {
		}
	}

	@Tag("container-tag")
	private static class TaggedTestContainer {

		@Property
		@Tag("property-tag")
		void propertyWithTag() {
		}

	}
}
