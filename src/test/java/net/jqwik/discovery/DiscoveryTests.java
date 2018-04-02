package net.jqwik.discovery;

import examples.packageWithErrors.*;
import examples.packageWithInheritance.*;
import examples.packageWithNestedContainers.*;
import examples.packageWithSeveralContainers.*;
import examples.packageWithSingleContainer.*;
import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.descriptor.*;
import net.jqwik.recording.*;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.*;
import org.junit.platform.launcher.*;

import java.util.concurrent.atomic.*;
import java.util.function.*;

import static net.jqwik.JqwikUniqueIdBuilder.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.*;

class DiscoveryTests {

	private final JqwikTestEngine testEngine = new JqwikTestEngine();
	private final TestRunData testRunData = new TestRunData();
	private final PropertyDefaultValues propertyDefaultValues = PropertyDefaultValues.with(1000, 5, 1000);
	private final UniqueId engineId = UniqueId.forEngine(testEngine.getId());

	private final Predicate<TestDescriptor> isEngineDescriptor = d -> d instanceof JqwikEngineDescriptor;
	private final Predicate<TestDescriptor> isClassDescriptor = d -> d instanceof ContainerClassDescriptor;
	private final Predicate<TestDescriptor> isPropertyDescriptor = d -> d.getClass().equals(PropertyMethodDescriptor.class);
	private final Predicate<TestDescriptor> isSkipDecorator = d -> d.getClass().equals(SkipExecutionDecorator.class);

	@Example
	void discoverFromPackage() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples.packageWithSeveralContainers")).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(count(engineDescriptor, isEngineDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(3);
		assertThat(count(engineDescriptor, isPropertyDescriptor)).isEqualTo(13);
	}

	@Example
	void discoverWithPackageNameFilter() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples"))
				.filters(PackageNameFilter.includePackageNames("examples.packageWithSeveralContainers")).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(count(engineDescriptor, isEngineDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(3);
		assertThat(count(engineDescriptor, isPropertyDescriptor)).isEqualTo(13);
	}

	@Example
	void discoverWithClassNameFilter() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples"))
				.filters(ClassNameFilter.includeClassNamePatterns(".+" + MixedTests.class.getSimpleName())).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(count(engineDescriptor, isEngineDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isPropertyDescriptor)).isEqualTo(5);
	}

	@Example
	void discoverFromClass() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(MixedTests.class)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(6);
	}

	@Example
	void discoverContainersWithinGroup() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(TopLevelContainerWithGroups.class)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(9);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(5);
	}

	@Example
	void discoverNestedContainerNotInGroup() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(TopLevelContainerWithNoGroups.NestedContainer.class))
				.build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(2);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(1);
	}

	@Example
	void discoverInnerContainerFromClass() {
		LauncherDiscoveryRequest discoveryRequest = request()
				.selectors(selectClass(TopLevelContainerWithGroups.InnerGroup.InnerInnerGroup.class)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(4);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(3);
		assertThat(count(engineDescriptor, isPropertyDescriptor)).isEqualTo(1);
	}

	@Example
	void discoverInnerContainerFromUniqueId() {
		UniqueId uniqueId = uniqueIdForClassContainer(TopLevelContainerWithGroups.class, TopLevelContainerWithGroups.InnerGroup.class,
				TopLevelContainerWithGroups.InnerGroup.InnerInnerGroup.class);
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectUniqueId(uniqueId)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(4);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(3);
		assertThat(count(engineDescriptor, isPropertyDescriptor)).isEqualTo(1);
	}

	@Example
	void discoverClassWithInheritance() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(ContainerWithInheritance.class)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isPropertyDescriptor)).isEqualTo(5);

		assertThat(count(engineDescriptor, isExample(AbstractContainer.class, "exampleToInherit"))).isEqualTo(1);
		assertThat(count(engineDescriptor, isExample(ContainerWithInheritance.class, "exampleToOverride"))).isEqualTo(1);
		assertThat(count(engineDescriptor, isExample(InterfaceTests.class, "exampleToInheritFromInterface"))).isEqualTo(1);
		assertThat(count(engineDescriptor, isExample(ContainerWithInheritance.class, "exampleToOverrideFromInterface"))).isEqualTo(1);
		assertThat(count(engineDescriptor, isExample(ContainerWithInheritance.class, "example"))).isEqualTo(1);

		assertThat(count(engineDescriptor, isExample(AbstractContainer.class, "exampleToDisable"))).isEqualTo(0);
		assertThat(count(engineDescriptor, isExample(ContainerWithInheritance.class, "exampleToDisable"))).isEqualTo(0);
	}

	@Example
	void discoverClassWithOverloadedExamples() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(ContainerWithOverloadedExamples.class)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);

		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(7);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isPropertyDescriptor)).isEqualTo(6);

		assertThat(count(engineDescriptor, isExample(ContainerWithOverloadedExamples.class, "succeeding"))).isEqualTo(1);
	}

	@Example
	void discoverFromMethod() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectMethod(SimpleExampleTests.class, "succeeding")).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(2);
	}

	@Example
	void discoverClassById() {
		UniqueId uniqueId = uniqueIdForClassContainer(SimpleExampleTests.class);
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectUniqueId(uniqueId)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(5);
	}

	@Example
	void discoverPropertyById() {
		UniqueId uniqueId = uniqueIdForPropertyMethod(SimpleExampleTests.class, "succeeding");
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectUniqueId(uniqueId)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(2);
	}

	@Group
	class Skipping {
		@Example
		void staticMethodIsSkipped() {
			LauncherDiscoveryRequest discoveryRequest = request().selectors(selectMethod(SimpleExampleTests.class, "staticExample"))
																 .build();

			TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
			assertThat(engineDescriptor.getDescendants().size()).isEqualTo(2);
			assertThat(count(engineDescriptor, isSkipDecorator)).isEqualTo(1);
		}

		@Example
		void methodWithJupiterAnnotationIsSkipped() {
			LauncherDiscoveryRequest discoveryRequest = request().selectors(selectMethod(SimpleExampleTests.class, "withJupiterAnnotation"))
																 .build();

			TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
			assertThat(engineDescriptor.getDescendants().size()).isEqualTo(2);
			assertThat(count(engineDescriptor, isSkipDecorator)).isEqualTo(1);
		}
	}

	private Predicate<TestDescriptor> isChildOf(Predicate<TestDescriptor> parentPredicate) {
		return descriptor -> {
			return descriptor.getParent().map(parentPredicate::test).orElse(false);
		};
	}

	private Predicate<TestDescriptor> isExample(Class<?> implementationClass, String methodName) {
		return descriptor -> {
			if (!isPropertyDescriptor.test(descriptor))
				return false;
			PropertyMethodDescriptor exampleDescriptor = (PropertyMethodDescriptor) descriptor;
			return exampleDescriptor.getTargetMethod().getName().equals(methodName)
					&& exampleDescriptor.getTargetMethod().getDeclaringClass().equals(implementationClass);
		};
	}

	private int count(TestDescriptor root, Predicate<TestDescriptor> descriptorTester) {
		AtomicInteger counter = new AtomicInteger();
		root.accept(d -> {
			if (descriptorTester.test(d))
				counter.incrementAndGet();
		});
		return counter.intValue();
	}

	private TestDescriptor discoverTests(LauncherDiscoveryRequest discoveryRequest) {
		JqwikEngineDescriptor engineDescriptor = new JqwikEngineDescriptor(engineId);
		new JqwikDiscoverer(testRunData, propertyDefaultValues).discover(discoveryRequest, engineDescriptor);
		return engineDescriptor;
	}
}
