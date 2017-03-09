package net.jqwik;

import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.PackageSelector;

public class JqwikTestEngine implements TestEngine {
	public static final String ENGINE_ID = "jqwik";

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
		Preconditions.notNull(discoveryRequest, "discovery request must not be null");
		JqwikEngineDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueId);
		discoveryRequest.getSelectorsByType(PackageSelector.class).forEach(packageSelector -> {
			if (!packageSelector.getPackageName().startsWith("net.jqwik"))
				engineDescriptor.addChild(new ExampleTestDescriptor(uniqueId));
		});
		return engineDescriptor;
	}

	@Override
	public void execute(ExecutionRequest request) {
		TestDescriptor root = request.getRootTestDescriptor();
		request.getEngineExecutionListener().executionStarted(root);
		request.getEngineExecutionListener().executionFinished(root, TestExecutionResult.successful());
	}

}
