package net.jqwik.engine.discovery;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.lifecycle.PropertyExecutionResult.*;
import net.jqwik.engine.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.recording.*;
import net.jqwik.engine.support.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.engine.TestHelper.*;

@Group
class PropertyMethodResolverTests {

	private final TestRunData testRunData = new TestRunData();
	private final PropertyAttributesDefaults propertyDefaultValues = TestHelper.propertyAttributesDefaults();
	private final PropertyMethodResolver resolver = new PropertyMethodResolver(testRunData, propertyDefaultValues);

	@Group
	class ResolveElement {
		@Example
		void plainProperty() {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);
			Method method = TestHelper.getMethod(TestContainer.class, "plainProperty");
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			assertThat(descriptors).hasSize(1);
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();

			assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("plainProperty");
			assertThat(propertyMethodDescriptor.getUniqueId())
				.isEqualTo(classDescriptor.getUniqueId().append("property", method.getName() + "()"));

			assertThat(propertyMethodDescriptor.getReporting()).isEmpty();

			assertDefaultConfigurationProperties(propertyMethodDescriptor);
		}

		@Example
		void propertyWithLabel() {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);
			Method method = TestHelper.getMethod(TestContainer.class, "propertyWithLabel");
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			assertThat(descriptors).hasSize(1);
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();

			assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("my label");
			assertThat(propertyMethodDescriptor.getUniqueId())
				.isEqualTo(classDescriptor.getUniqueId().append("property", method.getName() + "()"));

		}

		@Example
		void property_with_underscores() {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);
			Method method = TestHelper.getMethod(TestContainer.class, "property_with_underscores");
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			assertThat(descriptors).hasSize(1);
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();

			assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("property with underscores");
			assertThat(propertyMethodDescriptor.getUniqueId())
				.isEqualTo(classDescriptor.getUniqueId().append("property", method.getName() + "()"));

		}

		@Example
		void propertyWithTag() {
			PropertyMethodDescriptor propertyMethodDescriptor = resolveMethodInClass("propertyWithOneTag", TestContainer.class);

			assertThat(propertyMethodDescriptor.getTags()).containsExactly(TestTag.create("tag1"));
		}

		@Example
		void propertyWithSeveralTags() {
			PropertyMethodDescriptor propertyMethodDescriptor = resolveMethodInClass("propertyWithTwoTags", TestContainer.class);

			assertThat(propertyMethodDescriptor.getTags())
				.containsExactly(TestTag.create("tag1"), TestTag.create("tag2"));
		}

		@Example
		void propertyWithTaggedContainer() {
			PropertyMethodDescriptor propertyMethodDescriptor = resolveMethodInClass("propertyWithTag", TaggedTestContainer.class);

			assertThat(propertyMethodDescriptor.getTags())
				.containsExactlyInAnyOrder(TestTag.create("container-tag"), TestTag.create("property-tag"));
		}

		@Example
		void propertyWithDomains() {
			PropertyMethodDescriptor propertyMethodDescriptor = resolveMethodInClass("withDomains", TestContainer.class);

			Set<Domain> domains = propertyMethodDescriptor.getDomains();

			Stream<Class<? extends DomainContext>> domainClasses =
				domains
					.stream()
					.map(Domain::value);
			assertThat(domainClasses)
				.containsExactlyInAnyOrder(Domain1.class, Domain2.class);

			domains.stream().filter(d -> d.value().equals(Domain1.class))
				   .forEach(d -> assertThat(d.priority()).isEqualTo(42));
			domains.stream().filter(d -> d.value().equals(Domain2.class))
				   .forEach(d -> assertThat(d.priority()).isEqualTo(Domain.PRIORITY_NOT_SET));
		}

		@Example
		void propertyWithDomainInContainer() {
			PropertyMethodDescriptor propertyMethodDescriptor = resolveMethodInClass("withDomain1", ContainerWithDomain2.class);

			Stream<Class<? extends DomainContext>> domainClasses =
				propertyMethodDescriptor.getDomains()
										.stream()
										.map(Domain::value);
			assertThat(domainClasses)
				.containsExactlyInAnyOrder(Domain1.class, Domain2.class);
		}

		@Example
		void propertyWithParams() {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);
			Method method = TestHelper.getMethod(TestContainer.class, "propertyWithParams");
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			assertThat(descriptors).hasSize(1);
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();
			assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("propertyWithParams");
			assertThat(propertyMethodDescriptor.getUniqueId())
				.isEqualTo(classDescriptor.getUniqueId().append("property", "propertyWithParams(int, java.lang.String)"));
		}

		@Example
		void propertyWithAnnotationParams() {
			PropertyMethodDescriptor propertyMethodDescriptor = resolveMethodInClass("withPropertyParams", TestContainer.class);

			assertThat(propertyMethodDescriptor.getConfiguration().getSeed()).isEqualTo("42");
			assertThat(propertyMethodDescriptor.getConfiguration().getTries()).isEqualTo(99);
			assertThat(propertyMethodDescriptor.getConfiguration().getMaxDiscardRatio()).isEqualTo(6);
			assertThat(propertyMethodDescriptor.getConfiguration().getShrinkingMode()).isEqualTo(ShrinkingMode.OFF);
			assertThat(propertyMethodDescriptor.getConfiguration().getGenerationMode()).isEqualTo(GenerationMode.EXHAUSTIVE);
			assertThat(propertyMethodDescriptor.getConfiguration().getAfterFailureMode())
				.isEqualTo(AfterFailureMode.RANDOM_SEED);
		}

		@Example
		void propertyWithReportAnnotation() {
			PropertyMethodDescriptor propertyMethodDescriptor = resolveMethodInClass("withReportAnnotation", TestContainer.class);

			assertThat(propertyMethodDescriptor.getReporting()).containsExactly(Reporting.GENERATED, Reporting.FALSIFIED);
		}

		@Example
		void propertyThatPreviouslyFailed() {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);
			Method method = TestHelper.getMethod(TestContainer.class, "previouslyFailed");
			UniqueId previouslyFailedId = JqwikUniqueIDs.appendProperty(classDescriptor.getUniqueId(), method);
			List<Object> falsifiedSample = Arrays.asList("a", 1);
			testRunData.add(new TestRun(
				previouslyFailedId,
				new ParametersHash(method),
				Status.FAILED,
				new GenerationInfo("4243"),
				falsifiedSample
			));
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();
			assertThat(propertyMethodDescriptor.getConfiguration().getPreviousSeed()).isEqualTo("4243");
			assertThat(propertyMethodDescriptor.getConfiguration().getFalsifiedSample()).isEqualTo(falsifiedSample);
		}

		@Example
		void propertyThatPreviouslyFailedButWithDifferentAnnotations() {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);
			Method method = TestHelper.getMethod(TestContainer.class, "previouslyFailed");
			Method methodDifferentAnnotation = TestHelper.getMethod(TestContainer.class, "previouslyFailedDifferentAnnotation");
			UniqueId previouslyFailedId = JqwikUniqueIDs.appendProperty(classDescriptor.getUniqueId(), method);
			List<Object> falsifiedSample = Arrays.asList("a", 1);
			testRunData.add(new TestRun(
				previouslyFailedId,
				new ParametersHash(methodDifferentAnnotation),
				Status.FAILED,
				new GenerationInfo("4243"),
				falsifiedSample
			));
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();
			assertThat(propertyMethodDescriptor.getConfiguration().getPreviousSeed()).isEqualTo("4243");
			assertThat(propertyMethodDescriptor.getConfiguration().getFalsifiedSample()).isNull();
		}

		@Example
		void propertyThatDidNotPreviouslyFailWontHavePreviousSeed() {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);
			Method method = TestHelper.getMethod(TestContainer.class, "previouslyFailed");
			UniqueId previousId = JqwikUniqueIDs.appendProperty(classDescriptor.getUniqueId(), method);
			testRunData.add(new TestRun(previousId, new ParametersHash(method), Status.SUCCESSFUL, new GenerationInfo("4243"), null));
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();
			assertThat(propertyMethodDescriptor.getConfiguration().getPreviousSeed()).isNull();
			assertThat(propertyMethodDescriptor.getConfiguration().getFalsifiedSample()).isNull();
		}

		@Example
		void explicitSeedOverwritesSeedFromPreviouslyFailedTestRun() {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);
			Method method = TestHelper.getMethod(TestContainer.class, "withSeed41");
			UniqueId previouslyFailedId = JqwikUniqueIDs.appendProperty(classDescriptor.getUniqueId(), method);
			testRunData.add(new TestRun(previouslyFailedId, new ParametersHash(method), Status.FAILED, new GenerationInfo("9999"), null));
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptors.iterator().next();
			assertThat(propertyMethodDescriptor.getConfiguration().getSeed()).isEqualTo("41");
		}

		private PropertyMethodDescriptor resolveMethodInClass(String methodName, Class<?> containerClass) {
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(containerClass);
			Method method = TestHelper.getMethod(containerClass, methodName);
			Set<TestDescriptor> descriptors = resolver.resolveElement(method, classDescriptor);

			assertThat(descriptors).hasSize(1);
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
			assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("plainProperty");
			assertDefaultConfigurationProperties(propertyMethodDescriptor);
		}

		@Example
		void propertyWithParams() {
			Method method = TestHelper.getMethod(TestContainer.class, "propertyWithParams");
			UniqueId uniqueId = JqwikUniqueIDs.appendProperty(UniqueId.forEngine(JqwikTestEngine.ENGINE_ID), method);
			ContainerClassDescriptor classDescriptor = buildContainerDescriptor(TestContainer.class);

			Optional<TestDescriptor> descriptor = resolver.resolveUniqueId(uniqueId.getSegments().get(1), classDescriptor);

			assertThat(descriptor).isPresent();
			PropertyMethodDescriptor propertyMethodDescriptor = (PropertyMethodDescriptor) descriptor.get();
			assertThat(propertyMethodDescriptor.getLabel()).isEqualTo("propertyWithParams");
		}

	}

	private ContainerClassDescriptor buildContainerDescriptor(Class<?> containerClass) {
		return (ContainerClassDescriptor) TestDescriptorBuilder.forClass(containerClass).build();
	}

	private void assertDefaultConfigurationProperties(PropertyMethodDescriptor propertyMethodDescriptor) {
		assertThat(propertyMethodDescriptor.getConfiguration().getSeed()).isEmpty();
		assertThat(propertyMethodDescriptor.getConfiguration().getPreviousSeed()).isNull();
		assertThat(propertyMethodDescriptor.getConfiguration().getTries()).isEqualTo(DEFAULT_TRIES);
		assertThat(propertyMethodDescriptor.getConfiguration().getMaxDiscardRatio()).isEqualTo(DEFAULT_MAX_DISCARD_RATIO);
		assertThat(propertyMethodDescriptor.getConfiguration().getShrinkingMode()).isEqualTo(ShrinkingMode.BOUNDED);
		assertThat(propertyMethodDescriptor.getConfiguration().getGenerationMode()).isEqualTo(GenerationMode.AUTO);
		assertThat(propertyMethodDescriptor.getConfiguration().getAfterFailureMode()).isEqualTo(AfterFailureMode.PREVIOUS_SEED);
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
		void property_with_underscores() {
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
		void previouslyFailed(String aString) {
		}

		@Property
		void previouslyFailedDifferentAnnotation(@StringLength(0) String aString) {
		}

		@Property(seed = "41")
		void withSeed41() {
		}

		@Property(
			seed = "42",
			tries = 99,
			maxDiscardRatio = 6,
			shrinking = ShrinkingMode.OFF,
			generation = GenerationMode.EXHAUSTIVE,
			afterFailure = AfterFailureMode.RANDOM_SEED
		)
		void withPropertyParams() {
		}

		@Property
		@Report({Reporting.GENERATED, Reporting.FALSIFIED})
		void withReportAnnotation() {
		}

		@Property
		@Domain(value = Domain1.class, priority = 42)
		@Domain(Domain2.class)
		void withDomains() {
		}
	}

	@Tag("container-tag")
	private static class TaggedTestContainer {

		@Property
		@Tag("property-tag")
		void propertyWithTag() {
		}

	}

	@Domain(Domain2.class)
	private static class ContainerWithDomain2 {
		@Property
		@Domain(Domain1.class)
		void withDomain1() {
		}
	}

	private static class Domain1 extends DomainContextBase {}

	private static class Domain2 extends DomainContextBase {}
}
