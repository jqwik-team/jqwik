package net.jqwik.engine;

import java.util.function.*;

import org.junit.platform.engine.*;

import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.discovery.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.recording.*;
import net.jqwik.engine.support.*;

public class JqwikTestEngine implements TestEngine {
	public static final String ENGINE_ID = "jqwik";

	private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry();
	private JqwikConfiguration configuration;
	private Throwable startupThrowable = null;

	public JqwikTestEngine() {
		this(DefaultJqwikConfiguration::new);
	}

	JqwikTestEngine(Supplier<JqwikConfiguration> configurationSupplier) {
		try {
			this.configuration = configurationSupplier.get();
		} catch (Throwable engineStartupThrowable) {
			this.startupThrowable = engineStartupThrowable;
		}
	}

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
		// Throw exception caught during startup otherwise JUnit platform message hides original exception
		if (startupThrowable != null) {
			JqwikExceptionSupport.throwAsUncheckedException(startupThrowable);
		}

		JqwikEngineDescriptor engineDescriptor = new JqwikEngineDescriptor(
			uniqueId,
			configuration.testEngineConfiguration().previousRun(),
			configuration.propertyDefaultValues()
		);
//		new JqwikDiscoverer(configuration.testEngineConfiguration().previousRun(), configuration.propertyDefaultValues())
//			.discover(request, engineDescriptor);

		new JqwikDiscoverySelectorResolver().resolveSelectors(request, engineDescriptor);

		return engineDescriptor;
	}

	@Override
	public void execute(ExecutionRequest request) {
		TestDescriptor root = request.getRootTestDescriptor();
		EngineExecutionListener engineExecutionListener = request.getEngineExecutionListener();
		registerLifecycleHooks(root, request.getConfigurationParameters());
		executeTests(root, engineExecutionListener);
	}

	private void executeTests(TestDescriptor root, EngineExecutionListener listener) {
		try (TestRunRecorder recorder = configuration.testEngineConfiguration().recorder()) {
			new JqwikExecutor(
				lifecycleRegistry,
				recorder,
				configuration.testEngineConfiguration().previousFailures(),
				configuration.useJunitPlatformReporter(),
				configuration.reportOnlyFailures()
			).execute(root, listener);
		}
	}

	private void registerLifecycleHooks(TestDescriptor rootDescriptor, ConfigurationParameters configurationParameters) {
		new JqwikLifecycleRegistrator(lifecycleRegistry, configurationParameters).registerLifecycleHooks(rootDescriptor);
	}

}
