package net.jqwik.engine;

import java.util.function.*;
import java.util.logging.*;

import org.junit.platform.engine.*;

import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.discovery.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.recording.*;
import net.jqwik.engine.support.*;

public class JqwikTestEngine implements TestEngine {
	public static final String ENGINE_ID = "jqwik";

	private static final Logger LOG = Logger.getLogger(JqwikTestEngine.class.getName());

	private final LifecycleHooksRegistry lifecycleRegistry = new LifecycleHooksRegistry();
	private final Function<ConfigurationParameters,JqwikConfiguration> configurationSupplier;

	public JqwikTestEngine() {
		this(DefaultJqwikConfiguration::new);
	}

	JqwikTestEngine(Function<ConfigurationParameters,JqwikConfiguration> configurationSupplier) {
		this.configurationSupplier = configurationSupplier;
	}

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
		JqwikConfiguration configuration = buildConfiguration(request.getConfigurationParameters());
		TestDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueId, configuration);
		new JqwikDiscoverer(configuration.testEngineConfiguration().previousRun(), configuration.propertyDefaultValues())
			.discover(request, engineDescriptor);

		return engineDescriptor;
	}

	@Override
	public void execute(ExecutionRequest request) {
		// This cast is safe, as the JUnit engine javadoc states the root descriptor is the one returned from the discover method
		JqwikEngineDescriptor root = (JqwikEngineDescriptor) request.getRootTestDescriptor();
		EngineExecutionListener engineExecutionListener = request.getEngineExecutionListener();
		try {
			registerLifecycleHooks(root, request.getConfigurationParameters());
			executeTests(root, engineExecutionListener);
		} catch (Throwable throwable) {
			LOG.log(Level.SEVERE, throwable.getMessage(), throwable);
			//noinspection ResultOfMethodCallIgnored
			JqwikExceptionSupport.throwAsUncheckedException(throwable);
		}
	}

	private void executeTests(JqwikEngineDescriptor root, EngineExecutionListener listener) {
		JqwikConfiguration configuration = root.getConfiguration();
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

	private JqwikConfiguration buildConfiguration(ConfigurationParameters configurationParameters) {
		try {
			return configurationSupplier.apply(configurationParameters);
		} catch (Throwable engineStartupThrowable) {
			return JqwikExceptionSupport.throwAsUncheckedException(engineStartupThrowable);
		}
	}

}
