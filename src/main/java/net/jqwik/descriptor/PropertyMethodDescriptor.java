package net.jqwik.descriptor;

import net.jqwik.api.*;
import net.jqwik.execution.*;
import org.junit.platform.engine.*;

import java.lang.reflect.*;

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
		// TODO: Should be Type.CONTAINER_AND_TEST but then
		// IntelliJ does not display failures correctly in Test-Runner
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

	public ReportingMode getReportingMode() {
		return reportingMode;
	}
}
