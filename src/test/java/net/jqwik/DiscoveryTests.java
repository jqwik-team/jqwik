package net.jqwik;

import examples.packageWithErrors.ContainerWithOverloadedExamples;
import examples.packageWithInheritance.AbstractContainer;
import examples.packageWithInheritance.ContainerWithInheritance;
import examples.packageWithInheritance.InterfaceTests;
import examples.packageWithSingleContainer.SimpleExampleTests;
import net.jqwik.api.Example;
import net.jqwik.discovery.ContainerClassDescriptor;
import net.jqwik.discovery.ExampleMethodDescriptor;
import net.jqwik.discovery.JqwikDiscoverer;
import net.jqwik.discovery.OverloadedExamplesError;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.LauncherDiscoveryRequest;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

class DiscoveryTests {

	private final JqwikTestEngine testEngine = new JqwikTestEngine();
	private final UniqueId engineId = UniqueId.forEngine(testEngine.getId());

	private final Predicate<TestDescriptor> isEngineDescriptor = d -> d instanceof JqwikEngineDescriptor;
	private final Predicate<TestDescriptor> isClassDescriptor = d -> d instanceof ContainerClassDescriptor;
	private final Predicate<TestDescriptor> isExampleDescriptor = d -> d instanceof ExampleMethodDescriptor;
	private final Predicate<TestDescriptor> isErrorDescriptor = d -> d instanceof OverloadedExamplesError;

	@Example
	void discoverFromPackage() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples.packageWithSingleContainer")).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(count(engineDescriptor, isEngineDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isExampleDescriptor)).isEqualTo(2);
	}

	@Example
	void discoverFromClass() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(SimpleExampleTests.class)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(3);
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
		assertThat(count(engineDescriptor, isClassDescriptor)).isEqualTo(1);
		assertThat(count(engineDescriptor, isExampleDescriptor)).isEqualTo(4);
		assertThat(count(engineDescriptor, isErrorDescriptor)).isEqualTo(1);

		assertThat(count(engineDescriptor, isExample(ContainerWithOverloadedExamples.class, "succeeding"))).isEqualTo(1);
		assertThat(count(engineDescriptor, isOverloadedError(ContainerWithOverloadedExamples.class, "overloadedExample"))).isEqualTo(1);
		assertThat(count(engineDescriptor, isChildOf(isOverloadedError(ContainerWithOverloadedExamples.class, "overloadedExample"))))
				.isEqualTo(3);
	}

	@Example
	void discoverFromMethod() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectMethod(SimpleExampleTests.class, "succeeding")).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(2);
	}

	@Example
	void discoverClassById() {
		UniqueId classId = JqwikUniqueIdBuilder.uniqueIdForClassContainer(SimpleExampleTests.class);
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectUniqueId(classId)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(3);
	}

	@Example
	void discoverExampleById() {
		UniqueId classId = JqwikUniqueIdBuilder.uniqueIdForExampleMethod(SimpleExampleTests.class, "succeeding");
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectUniqueId(classId)).build();

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
			return exampleDescriptor.getExampleMethod().getName().equals(methodName)
					&& exampleDescriptor.getExampleMethod().getDeclaringClass().equals(implementationClass);
		};
	}

	private Predicate<TestDescriptor> isOverloadedError(Class<?> implementationClass, String methodName) {
		return descriptor -> {
			if (!isErrorDescriptor.test(descriptor))
				return false;
			OverloadedExamplesError errorDescriptor = (OverloadedExamplesError) descriptor;
			return errorDescriptor.getOverloadedMethodName().equals(methodName)
					&& errorDescriptor.getContainerClass().equals(implementationClass);
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
