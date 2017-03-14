package net.jqwik;

import net.jqwik.descriptor.JqwikEngineDescriptor;
import net.jqwik.discovery.JqwikDiscoverer;
import org.junit.platform.engine.*;

import net.jqwik.execution.JqwikExecutor;
import net.jqwik.execution.LifecycleRegistry;

public class JqwikTestEngine implements TestEngine {
	public static final String ENGINE_ID = "jqwik";

	private final LifecycleRegistry registry = new LifecycleRegistry();

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
		TestDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueId);
		new JqwikDiscoverer().discover(request, engineDescriptor);
		return engineDescriptor;
	}

	@Override
	public void execute(ExecutionRequest request) {
		TestDescriptor root = request.getRootTestDescriptor();
		new JqwikExecutor(registry).execute(request, root);
	}

}
