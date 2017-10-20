package net.jqwik.descriptor;

import net.jqwik.api.*;
import org.junit.platform.engine.*;

import java.lang.reflect.*;

public class PropertyMethodDescriptor extends AbstractMethodDescriptor implements PropertyContext {

	private final long seed;
	private final int tries;
	private final int maxDiscardRatio;
	private final ShrinkingMode shrinkingMode;

	public PropertyMethodDescriptor(UniqueId uniqueId, Method propertyMethod, Class containerClass, long seed, int tries, int maxDiscardRatio, ShrinkingMode shrinkingMode) {
		super(uniqueId, propertyMethod, containerClass);
		this.seed = seed;
		this.tries = tries;
		this.maxDiscardRatio = maxDiscardRatio;
		this.shrinkingMode = shrinkingMode;
	}

	@Override
	//TODO: Change to CONTAINER_AND_TEST as soon as https://github.com/junit-team/junit5/issues/756 has been fixed
	public Type getType() {
		return Type.TEST;
	}

	public long getSeed() {
		return seed;
	}

	public int getTries() {
		return tries;
	}

	public int getMaxDiscardRatio() {
		return maxDiscardRatio;
	}

	public ShrinkingMode getShrinkingMode() {
		return shrinkingMode;
	}
}
