package net.jqwik;

import java.util.Optional;

import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.engine.*;
import org.junit.platform.engine.discovery.PackageSelector;
import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;

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
			if (packageSelector.getPackageName().contains("examples"))
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
