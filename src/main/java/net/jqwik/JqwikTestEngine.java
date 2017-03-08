package net.jqwik;

import java.util.Optional;

import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;

public class JqwikTestEngine extends HierarchicalTestEngine {
	public static final String ENGINE_ID = "jqwik";

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	public Optional<String> getGroupId() {
		return Optional.of("net.jqwik");
	}

	@Override
	public Optional<String> getArtifactId() {
		return Optional.of("jqwik-engine");
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
		Preconditions.notNull(discoveryRequest, "discovery request must not be null");
		JqwikEngineDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueId);
		return engineDescriptor;
	}

	@Override
	protected EngineExecutionContext createExecutionContext(ExecutionRequest request) {
		return new EngineExecutionContext() {
		};
	}

}
