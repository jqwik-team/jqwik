package net.jqwik;

import net.jqwik.descriptor.*;
import net.jqwik.discovery.*;
import net.jqwik.execution.*;
import net.jqwik.recording.*;
import org.junit.platform.engine.*;

import java.nio.file.*;

public class JqwikTestEngine implements TestEngine {
	public static final String ENGINE_ID = "jqwik";

	private final LifecycleRegistry registry = new LifecycleRegistry();
	private final TestRunDatabase database;

	public JqwikTestEngine() {
		this(createTestRunDatabase());
	}

	private static TestRunDatabase createTestRunDatabase() {
		JqwikProperties properties = new JqwikProperties();
		return new TestRunDatabase(properties.getDatabasePath());
	}

	public JqwikTestEngine(TestRunDatabase database) {
		this.database = database;
	}

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
		TestDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueId);
		new JqwikDiscoverer(database.previousRun()).discover(request, engineDescriptor);
		return engineDescriptor;
	}

	@Override
	public void execute(ExecutionRequest request) {
		TestDescriptor root = request.getRootTestDescriptor();
		try(TestRunRecorder recorder = database.recorder()) {
			new JqwikExecutor(registry, recorder, database.previousRun()).execute(request, root);
		}
	}

}
