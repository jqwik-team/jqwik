package net.jqwik.engine.descriptor;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.engine.*;

public class JqwikEngineDescriptor extends EngineDescriptor {
	private static final String DISPLAY_NAME = "jqwik (JUnit Platform)";
	private final JqwikConfiguration configuration;

	public JqwikEngineDescriptor(UniqueId uniqueId, JqwikConfiguration configuration) {
		super(uniqueId, DISPLAY_NAME);
		this.configuration = configuration;
	}

	public JqwikConfiguration getConfiguration() {
		return configuration;
	}
}
