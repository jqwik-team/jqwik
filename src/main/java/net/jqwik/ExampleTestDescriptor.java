package net.jqwik;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;

public class ExampleTestDescriptor extends AbstractTestDescriptor {

	public ExampleTestDescriptor(UniqueId parentid) {
		super(parentid.append("example", "aTestExample"), "a test example");
	}

	@Override
	public boolean isContainer() {
		return false;
	}

	@Override
	public boolean isTest() {
		return true;
	}
}
