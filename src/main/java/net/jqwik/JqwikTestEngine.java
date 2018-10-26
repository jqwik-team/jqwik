package net.jqwik;

import org.junit.platform.engine.*;

import net.jqwik.descriptor.*;
import net.jqwik.discovery.*;
import net.jqwik.execution.*;
import net.jqwik.execution.lifecycle.*;
import net.jqwik.recording.*;

public class JqwikTestEngine implements TestEngine {
	public static final String ENGINE_ID = "jqwik";

	private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry();
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
		registerLifecycleHooks(root);
		executeTests(root, request.getEngineExecutionListener());
	}

	private void executeTests(TestDescriptor root, EngineExecutionListener listener) {
		try (TestRunRecorder recorder = configuration.testEngineConfiguration().recorder()) {
			new JqwikExecutor(lifecycleRegistry, recorder, configuration.testEngineConfiguration().previousFailures(), configuration.useJunitPlatformReporter())
				.execute(root, listener);
		}
	}

	private void registerLifecycleHooks(TestDescriptor rootDescriptor) {
		new JqwikLifecycleRegistrator(lifecycleRegistry).registerLifecycleHooks(rootDescriptor);
	}

}
