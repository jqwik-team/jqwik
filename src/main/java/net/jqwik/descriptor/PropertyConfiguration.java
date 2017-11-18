package net.jqwik.descriptor;

import net.jqwik.api.*;

public class PropertyConfiguration {

	public static PropertyConfiguration from(Property property) {
		return new PropertyConfiguration(property.seed(), property.tries(), property.maxDiscardRatio(), property.shrinking(),
				property.reporting());
	}

	private final long seed;
	private final int tries;
	private final int maxDiscardRatio;
	private final ShrinkingMode shrinkingMode;
	private final ReportingMode reportingMode;

	public PropertyConfiguration( //
			long seed, //
			int tries, //
			int maxDiscardRatio, //
			ShrinkingMode shrinkingMode, //
			ReportingMode reportingMode //
	) {
		this.seed = seed;
		this.tries = tries;
		this.maxDiscardRatio = maxDiscardRatio;
		this.shrinkingMode = shrinkingMode;
		this.reportingMode = reportingMode;
	}

	public long getSeed() {
		return seed;
	}

	public PropertyConfiguration withSeed(long changedSeed) {
		return new PropertyConfiguration(changedSeed, this.tries, this.maxDiscardRatio, this.shrinkingMode, this.reportingMode);
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
