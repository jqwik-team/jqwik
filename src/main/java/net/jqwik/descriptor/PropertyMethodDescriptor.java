package net.jqwik.descriptor;

import java.lang.reflect.Method;

import org.junit.platform.engine.UniqueId;

import net.jqwik.api.*;
import net.jqwik.execution.PropertyContext;

public class PropertyMethodDescriptor extends AbstractMethodDescriptor implements PropertyContext {

	private final long seed;
	private final int tries;
	private final int maxDiscardRatio;
	private final ShrinkingMode shrinkingMode;
	private final ReportingMode reportingMode;

	public PropertyMethodDescriptor(UniqueId uniqueId, Method propertyMethod, Class containerClass, long seed, int tries,
			int maxDiscardRatio, ShrinkingMode shrinkingMode, ReportingMode reportingMode) {
		super(uniqueId, propertyMethod, containerClass);
		this.seed = seed;
		this.tries = tries;
		this.maxDiscardRatio = maxDiscardRatio;
		this.shrinkingMode = shrinkingMode;
		this.reportingMode = reportingMode;
	}

	@Override
	public Type getType() {
		return Type.CONTAINER_AND_TEST;
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

	public ReportingMode getReportingMode() {
		return reportingMode;
	}
}
