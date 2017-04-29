package net.jqwik;

import org.junit.platform.engine.*;

import net.jqwik.descriptor.*;
import net.jqwik.discovery.*;
import net.jqwik.execution.*;
import net.jqwik.recording.*;

public class JqwikTestEngine implements TestEngine {
	public static final String ENGINE_ID = "jqwik";

	private final LifecycleRegistry registry = new LifecycleRegistry();
	private final TestEngineConfiguration configuration;

	public JqwikTestEngine() {
		this(createTestEngineConfiguration());
	}

	private static TestEngineConfiguration createTestEngineConfiguration() {
		JqwikProperties properties = new JqwikProperties();
		return properties.testEngineConfiguration();
	}

	JqwikTestEngine(TestEngineConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
		TestDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueId);
		new JqwikDiscoverer(configuration.previousRun()).discover(request, engineDescriptor);
		return engineDescriptor;
	}

	@Override
	public void execute(ExecutionRequest request) {
		TestDescriptor root = request.getRootTestDescriptor();
		try (TestRunRecorder recorder = configuration.recorder()) {
			new JqwikExecutor(registry, recorder, configuration.previousFailures()).execute(root, request.getEngineExecutionListener());
		}
	}

}
