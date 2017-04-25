package net.jqwik;

import org.junit.platform.engine.*;

import net.jqwik.descriptor.*;
import net.jqwik.discovery.*;
import net.jqwik.execution.*;

public class JqwikTestEngine implements TestEngine {
	public static final String ENGINE_ID = "jqwik";

	private final LifecycleRegistry registry = new LifecycleRegistry();
	private final TestRunData testRunDatabase = new TestRunData();

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
		TestDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueId);
		new JqwikDiscoverer(testRunDatabase).discover(request, engineDescriptor);
		return engineDescriptor;
	}

	@Override
	public void execute(ExecutionRequest request) {
		TestDescriptor root = request.getRootTestDescriptor();
		new JqwikExecutor(registry).execute(request, root);
	}

}
