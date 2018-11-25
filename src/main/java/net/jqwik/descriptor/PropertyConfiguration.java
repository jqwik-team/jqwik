package net.jqwik.descriptor;

import net.jqwik.*;
import net.jqwik.api.*;

public class PropertyConfiguration {

	public static PropertyConfiguration from(Property property, PropertyDefaultValues propertyDefaultValues, String previousSeed) {
		int tries = property.tries() == Property.TRIES_NOT_SET //
				? propertyDefaultValues.tries()
				: property.tries();
		int maxDiscardRatio = property.maxDiscardRatio() == Property.MAX_DISCARD_RATIO_NOT_SET //
				? propertyDefaultValues.maxDiscardRatio()
				: property.maxDiscardRatio();
		return new PropertyConfiguration(
			property.stereotype(),
			property.seed(),
			previousSeed,
			tries,
			maxDiscardRatio,
			property.shrinking(),
			property.generation(),
			property.afterFailure()
		);
	}

	private final String stereotype;
	private final String seed;
	private final String previousSeed;
	private final int tries;
	private final int maxDiscardRatio;
	private final ShrinkingMode shrinkingMode;
	private final GenerationMode generationMode;
	private final AfterFailureMode afterFailureMode;

	public PropertyConfiguration(
		String stereotype,
		String seed,
		String previousSeed,
		int tries,
		int maxDiscardRatio,
		ShrinkingMode shrinkingMode,
		GenerationMode generationMode,
		AfterFailureMode afterFailureMode
	) {
		this.stereotype = stereotype;
		this.seed = seed;
		this.previousSeed = previousSeed;
		this.tries = tries;
		this.maxDiscardRatio = maxDiscardRatio;
		this.shrinkingMode = shrinkingMode;
		this.generationMode = generationMode;
		this.afterFailureMode = afterFailureMode;
	}

	public String getSeed() {
		return seed;
	}

	public String getPreviousSeed() {
		return previousSeed;
	}

	public PropertyConfiguration withSeed(String changedSeed) {
		return new PropertyConfiguration(this.stereotype, changedSeed, this.previousSeed, this.tries, this.maxDiscardRatio, this.shrinkingMode, this.generationMode, this.afterFailureMode);
	}

	public PropertyConfiguration withGenerationMode(GenerationMode changedGenerationMode) {
		return new PropertyConfiguration(this.stereotype, this.seed, this.previousSeed, this.tries, this.maxDiscardRatio, this.shrinkingMode, changedGenerationMode, this.afterFailureMode);
	}

	public PropertyConfiguration withTries(int changedTries) {
		return new PropertyConfiguration(this.stereotype, this.seed, this.previousSeed, changedTries, this.maxDiscardRatio, this.shrinkingMode, this.generationMode, this.afterFailureMode);
	}

	public String getStereotype() {
		return stereotype;
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

	public GenerationMode getGenerationMode() {
		return generationMode;
	}

	public AfterFailureMode getAfterFailureMode() {
		return afterFailureMode;
	}




}
