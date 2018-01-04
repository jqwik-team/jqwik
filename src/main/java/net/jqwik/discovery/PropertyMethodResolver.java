package net.jqwik.discovery;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.engine.*;

import net.jqwik.api.Property;
import net.jqwik.descriptor.*;
import net.jqwik.discovery.specs.PropertyDiscoverySpec;
import net.jqwik.recording.*;

class PropertyMethodResolver implements ElementResolver {

	private final PropertyDiscoverySpec methodSpec = new PropertyDiscoverySpec();
	private final TestRunData testRunData;
	private final PropertyDefaultValues propertyDefaultValues;

	PropertyMethodResolver(TestRunData testRunData, PropertyDefaultValues propertyDefaultValues) {
		this.testRunData = testRunData;
		this.propertyDefaultValues = propertyDefaultValues;
	}

	@Override
	public Set<TestDescriptor> resolveElement(AnnotatedElement element, TestDescriptor parent) {
		if (!(element instanceof Method))
			return Collections.emptySet();

		if (!(parent instanceof ContainerClassDescriptor))
			return Collections.emptySet();

		Method method = (Method) element;
		if (!isRelevantMethod(method))
			return Collections.emptySet();

		return Collections.singleton(createTestDescriptor(parent, method));
	}

	@Override
	public Optional<TestDescriptor> resolveUniqueId(UniqueId.Segment segment, TestDescriptor parent) {
		if (!segment.getType().equals(getSegmentType()))
			return Optional.empty();

		if (!(parent instanceof ContainerClassDescriptor))
			return Optional.empty();

		Optional<Method> optionalMethod = findMethod(segment, (ContainerClassDescriptor) parent);
		if (!optionalMethod.isPresent())
			return Optional.empty();

		Method method = optionalMethod.get();
		if (!isRelevantMethod(method))
			return Optional.empty();

		return Optional.of(createTestDescriptor(parent, method));
	}

	private boolean isRelevantMethod(Method candidate) {
		return methodSpec.shouldBeDiscovered(candidate);
	}

	private Optional<Method> findMethod(UniqueId.Segment segment, ContainerClassDescriptor parent) {
		return JqwikUniqueIDs.findMethodBySegment(segment, parent.getContainerClass());
	}

	private TestDescriptor createTestDescriptor(TestDescriptor parent, Method method) {
		UniqueId uniqueId = createUniqueId(method, parent);
		Class<?> testClass = ((ContainerClassDescriptor) parent).getContainerClass();
		TestDescriptor newDescriptor = createTestDescriptor(uniqueId, testClass, method);
		if (methodSpec.butSkippedOnExecution(method)) {
			return new SkipExecutionDecorator(newDescriptor, methodSpec.skippingReason(method));
		} else {
			return newDescriptor;
		}
	}

	private TestDescriptor createTestDescriptor(UniqueId uniqueId, Class<?> testClass, Method method) {
		Property property = AnnotationSupport.findAnnotation(method, Property.class).orElseThrow(() -> {
			String message = String.format("Method [%s] is not annotated with @Property", method);
			return new JqwikException(message);
		});
		long seed = determineSeed(uniqueId, property.seed());
		PropertyConfiguration propertyConfig = PropertyConfiguration.from(property, propertyDefaultValues).withSeed(seed);
		return new PropertyMethodDescriptor(uniqueId, method, testClass, propertyConfig);
	}

	private long determineSeed(UniqueId uniqueId, long seedFromProperty) {
		return testRunData.byUniqueId(uniqueId) //
						  .filter(testRunData -> testRunData.getStatus() != TestExecutionResult.Status.SUCCESSFUL) //
						  .map(TestRun::getRandomSeed) //
						  .map(seedFromFailedRun -> seedFromProperty != Property.SEED_NOT_SET ? seedFromProperty : seedFromFailedRun) //
						  .orElse(seedFromProperty);
	}

	private String getSegmentType() {
		return JqwikUniqueIDs.PROPERTY_SEGMENT_TYPE;
	}

	private UniqueId createUniqueId(Method method, TestDescriptor parent) {
		return JqwikUniqueIDs.appendProperty(parent.getUniqueId(), method);
	}
}
