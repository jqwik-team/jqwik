package net.jqwik.descriptor;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.descriptor.*;

public class SeededFailedPropertyDescriptor extends AbstractTestDescriptor {


	public SeededFailedPropertyDescriptor(UniqueId uniqueId, long seed) {
		super(uniqueId, determineDisplayName(seed));
	}

	private static String determineDisplayName(long seed) {
		return String.format("Failed with seed: %s", seed);
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}
}
