package net.jqwik.engine.descriptor;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

import net.jqwik.engine.*;
import net.jqwik.engine.recording.*;

public class JqwikEngineDescriptor extends EngineDescriptor {
	private static final String DISPLAY_NAME = "jqwik (JUnit Platform)";

	private final TestRunData testRunData;
	private final PropertyDefaultValues propertyDefaultValues;

	public TestRunData getTestRunData() {
		return testRunData;
	}

	public PropertyDefaultValues getPropertyDefaultValues() {
		return propertyDefaultValues;
	}

	public JqwikEngineDescriptor(
		UniqueId uniqueId,
		TestRunData testRunData,
		PropertyDefaultValues propertyDefaultValues
	) {
		super(uniqueId, DISPLAY_NAME);
		this.testRunData = testRunData;
		this.propertyDefaultValues = propertyDefaultValues;
	}
}
