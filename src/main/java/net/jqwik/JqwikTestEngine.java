package net.jqwik;

import org.junit.platform.engine.*;

import net.jqwik.descriptor.*;
import net.jqwik.discovery.*;
import net.jqwik.execution.*;
import net.jqwik.recording.*;

public class JqwikTestEngine implements TestEngine {
	public static final String ENGINE_ID = "jqwik";

	private final LifecycleRegistry registry = new LifecycleRegistry();
	private final JqwikConfiguration configuration;

	public JqwikTestEngine() {
		this(new DefaultJqwikConfiguration());
	}

	JqwikTestEngine(JqwikConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
		TestDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueId);
		new JqwikDiscoverer(configuration.testEngineConfiguration().previousRun(), configuration.propertyDefaultValues()) //
			.discover(request, engineDescriptor);
		return engineDescriptor;
	}

	@Override
	public void execute(ExecutionRequest request) {
		TestDescriptor root = request.getRootTestDescriptor();
		try (TestRunRecorder recorder = configuration.testEngineConfiguration().recorder()) {
			new JqwikExecutor(registry, recorder, configuration.testEngineConfiguration().previousFailures()) //
					.execute(root, request.getEngineExecutionListener());
		}
	}

}
