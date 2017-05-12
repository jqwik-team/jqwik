package net.jqwik.descriptor;

import java.lang.reflect.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;

public class PropertyMethodDescriptor extends AbstractMethodDescriptor implements PropertyContext {

	private final long seed;
	private final int tries;

	public PropertyMethodDescriptor(UniqueId uniqueId, Method propertyMethod, Class containerClass, long seed, int tries) {
		super(uniqueId, propertyMethod, containerClass);
		this.seed = seed;
		this.tries = tries;
	}

	@Override
	//TODO: Change to CONTAINER_AND_TEST as soon as https://github.com/junit-team/junit5/issues/756 has been fixed
	public Type getType() {
		return Type.TEST;
	}

	@Override
	//TODO: Remove as soon as https://github.com/junit-team/junit5/issues/756 has been fixed
	public boolean hasTests() {
		return true;
	}

	public long getSeed() {
		return seed;
	}

	public int getTries() {
		return tries;
	}
}
