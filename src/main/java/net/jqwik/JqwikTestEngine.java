package net.jqwik;

import net.jqwik.execution.JqwikExecutor;
import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

import net.jqwik.discovery.JqwikDiscoverer;

public class JqwikTestEngine implements TestEngine {
	public static final String ENGINE_ID = "jqwik";

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
		new JqwikExecutor().execute(request, root);
	}

}
