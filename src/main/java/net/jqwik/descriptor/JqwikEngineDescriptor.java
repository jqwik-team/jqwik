package net.jqwik.descriptor;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

public class JqwikEngineDescriptor extends EngineDescriptor {
	private static final String DISPLAY_NAME = "jqwik for Java";

	public JqwikEngineDescriptor(UniqueId uniqueId) {
		super(uniqueId, DISPLAY_NAME);
	}
}
