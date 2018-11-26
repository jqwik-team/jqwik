package net.jqwik;

import java.util.function.*;

import org.junit.platform.commons.util.*;
import org.junit.platform.engine.*;

import net.jqwik.descriptor.*;
import net.jqwik.discovery.*;
import net.jqwik.execution.*;
import net.jqwik.execution.lifecycle.*;
import net.jqwik.recording.*;

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
		TestDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueId);

		// Throw exception caught during startup otherwise JUnit platform message hides original exception
		if (startupThrowable != null) {
			ExceptionUtils.throwAsUncheckedException(startupThrowable);
		}

		new JqwikDiscoverer(configuration.testEngineConfiguration().previousRun(), configuration.propertyDefaultValues()) //
			.discover(request, engineDescriptor);
		return engineDescriptor;
	}

	@Override
	public void execute(ExecutionRequest request) {
		TestDescriptor root = request.getRootTestDescriptor();
		EngineExecutionListener engineExecutionListener = request.getEngineExecutionListener();
		registerLifecycleHooks(root);
		executeTests(root, engineExecutionListener);
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
