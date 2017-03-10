package net.jqwik;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.request;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.launcher.LauncherDiscoveryRequest;

import examples.packageWithSingleContainer.SimpleExampleTests;
import net.jqwik.api.Example;
import net.jqwik.discovery.JqwikDiscoverer;

class DiscoveryTests {

	private final JqwikTestEngine testEngine = new JqwikTestEngine();
	private final UniqueId engineId = UniqueId.forEngine(testEngine.getId());

	@Example
	void discoverFromPackage() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectPackage("examples.packageWithSingleContainer")).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(3);

	}

	@Example
	void discoverFromClass() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectClass(SimpleExampleTests.class)).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(3);
	}

	@Example
	void discoverFromMethod() {
		LauncherDiscoveryRequest discoveryRequest = request().selectors(selectMethod(SimpleExampleTests.class, "succeeding")).build();

		TestDescriptor engineDescriptor = discoverTests(discoveryRequest);
		assertThat(engineDescriptor.getDescendants().size()).isEqualTo(2);
	}

	private TestDescriptor discoverTests(LauncherDiscoveryRequest discoveryRequest) {
		JqwikEngineDescriptor engineDescriptor = new JqwikEngineDescriptor(engineId);
		new JqwikDiscoverer().discover(discoveryRequest, engineDescriptor);
		return engineDescriptor;
	}
}
