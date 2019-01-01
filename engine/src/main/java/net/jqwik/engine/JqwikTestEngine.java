package net.jqwik.engine;

import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.util.*;
import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.discovery.*;
import net.jqwik.engine.execution.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.recording.*;

public class JqwikTestEngine implements TestEngine {
	public static final String ENGINE_ID = "jqwik";

	private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry();
	private JqwikConfiguration configuration;
	private Throwable startupThrowable = null;
	private Tuple.Tuple2<EngineDiscoveryRequest, TestDescriptor> discoveryCache = null;

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
			ExceptionUtils.throwAsUncheckedException(startupThrowable);
		}

		Optional<TestDescriptor> cachedTestDescriptor = getCachedDescriptor(request);

		// TODO: Remove caching as soon as https://github.com/junit-team/junit5/issues/1695 has been resolved
		if (cachedTestDescriptor.isPresent()) {
			return cachedTestDescriptor.get();
		}

		TestDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueId);
		new JqwikDiscoverer(configuration.testEngineConfiguration().previousRun(), configuration.propertyDefaultValues())
			.discover(request, engineDescriptor);

		// TODO: Remove caching as soon as https://github.com/junit-team/junit5/issues/1695 has been resolved
		return cacheDescriptor(request, engineDescriptor);
	}

	private TestDescriptor cacheDescriptor(EngineDiscoveryRequest request, TestDescriptor engineDescriptor) {
		discoveryCache = Tuple.of(request, engineDescriptor);
		return engineDescriptor;
	}

	private Optional<TestDescriptor> getCachedDescriptor(EngineDiscoveryRequest request) {
		// Assumption: instances of EngineDiscoveryRequest are immutable so that
		// the same instance will always mean the same thing
		if (discoveryCache != null && discoveryCache.get1() == request) {
			return Optional.of(discoveryCache.get2());
		}
		return Optional.empty();
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
				configuration.useJunitPlatformReporter()
			).execute(root, listener);
		}
	}

	private void registerLifecycleHooks(
		TestDescriptor rootDescriptor, ConfigurationParameters configurationParameters
	) {
		new JqwikLifecycleRegistrator(lifecycleRegistry, configurationParameters).registerLifecycleHooks(rootDescriptor);
	}

}
