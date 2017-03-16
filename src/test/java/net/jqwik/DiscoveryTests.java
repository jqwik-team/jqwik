package net.jqwik;

import static net.jqwik.JqwikUniqueIdBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassNameFilter;
import org.junit.platform.engine.discovery.PackageNameFilter;
import org.junit.platform.launcher.LauncherDiscoveryRequest;

import examples.packageWithErrors.ContainerWithOverloadedExamples;
import examples.packageWithInheritance.AbstractContainer;
import examples.packageWithInheritance.ContainerWithInheritance;
import examples.packageWithInheritance.InterfaceTests;
import examples.packageWithNestedContainers.TopLevelContainer;
import examples.packageWithNestedContainers.TopLevelGroup;
import examples.packageWithSeveralContainers.MixedTests;
import examples.packageWithSingleContainer.SimpleExampleTests;
import net.jqwik.api.Example;
import net.jqwik.descriptor.ContainerClassDescriptor;
import net.jqwik.descriptor.ExampleMethodDescriptor;
import net.jqwik.descriptor.JqwikEngineDescriptor;
import net.jqwik.descriptor.PropertyMethodDescriptor;
import net.jqwik.discovery.JqwikDiscoverer;

class DiscoveryTests {

	private final JqwikTestEngine testEngine = new JqwikTestEngine();
	private final UniqueId engineId = UniqueId.forEngine(testEngine.getId());

	private final Predicate<TestDescriptor> isEngineDescriptor = d -> d instanceof JqwikEngineDescriptor;
	private final Predicate<TestDescriptor> isClassDescriptor = d -> d instanceof ContainerClassDescriptor;
	private final Predicate<TestDescriptor> isExampleDescriptor = d -> d.getClass().equals(ExampleMethodDescriptor.class);
	private final Predicate<TestDescriptor> isPropertyDescriptor = d -> d.getClass().equals(PropertyMethodDescriptor.class);

	@Example
	void discoverFromPackage() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples.packageWithSeveralContainers")).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(count(engineDescriptor, isEngineDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(3);
		assertThat(count(engineDescriptor, isExampleDescriptor)).isEqualTo(3);
		assertThat(count(engineDescriptor, isPropertyDescriptor)).isEqualTo(5);
	}

	@Example
	void discoverWithPackageNameFilter() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples"))
				.filters(PackageNameFilter.includePackageNames("examples.packageWithSeveralContainers")).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(count(engineDescriptor, isEngineDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(3);
		assertThat(count(engineDescriptor, isExampleDescriptor)).isEqualTo(3);
		assertThat(count(engineDescriptor, isPropertyDescriptor)).isEqualTo(5);
	}

	@Example
	void discoverWithClassNameFilter() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples"))
				.filters(ClassNameFilter.includeClassNamePatterns(".+" + MixedTests.class.getSimpleName())).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(count(engineDescriptor, isEngineDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isExampleDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isPropertyDescriptor)).isEqualTo(1);
	}

	@Example
	void discoverFromClass() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(MixedTests.class)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(3);
	}

	@Example
	void discoverContainersWithinGroup() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(TopLevelGroup.class)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(9);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(5);
	}

	@Example
	void discoverNestedContainerNotInGroup() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(TopLevelContainer.NestedContainer.class)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(2);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(1);
	}

	@Example
	void discoverInnerContainerFromClass() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(TopLevelGroup.InnerGroup.InnerInnerContainer.class))
				.build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(4);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(3);
		assertThat(count(engineDescriptor, isPropertyDescriptor)).isEqualTo(1);
	}

	@Example
	void discoverInnerContainerFromUniqueId() {
		UniqueId uniqueId = uniqueIdForClassContainer(TopLevelGroup.class, TopLevelGroup.InnerGroup.class,
				TopLevelGroup.InnerGroup.InnerInnerContainer.class);
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
		assertThat(count(engineDescriptor, isExampleDescriptor)).isEqualTo(5);

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
		assertThat(count(engineDescriptor, isExampleDescriptor)).isEqualTo(4);
		assertThat(count(engineDescriptor, isPropertyDescriptor)).isEqualTo(2);

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
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(3);
	}

	@Example
	void discoverExampleById() {
		UniqueId uniqueId = uniqueIdForExampleMethod(SimpleExampleTests.class, "succeeding");
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectUniqueId(uniqueId)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(2);
	}

	private Predicate<TestDescriptor> isChildOf(Predicate<TestDescriptor> parentPredicate) {
		return descriptor -> {
			return descriptor.getParent().map(parentPredicate::test).orElse(false);
		};
	}

	private Predicate<TestDescriptor> isExample(Class<?> implementationClass, String methodName) {
		return descriptor -> {
			if (!isExampleDescriptor.test(descriptor))
				return false;
			ExampleMethodDescriptor exampleDescriptor = (ExampleMethodDescriptor) descriptor;
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
		new JqwikDiscoverer().discover(discoveryRequest, engineDescriptor);
		return engineDescriptor;
	}
}
